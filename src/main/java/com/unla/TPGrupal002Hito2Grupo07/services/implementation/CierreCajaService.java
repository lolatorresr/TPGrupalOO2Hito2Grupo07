package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.dtos.CierreCajaDTO;
import com.unla.TPGrupal002Hito2Grupo07.entities.*;
import com.unla.TPGrupal002Hito2Grupo07.repositories.ICierreCajaRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPedidoRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPersonaRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IUnidadDeVentaRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.ICierreCajaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service("cierreCajaService")
public class CierreCajaService implements ICierreCajaService {

    private final ICierreCajaRepository cierreCajaRepository;
    private final IPedidoRepository pedidoRepository;
    private final IPersonaRepository personaRepository;
    private final IUnidadDeVentaRepository unidadRepository;

    public CierreCajaService(ICierreCajaRepository cierreCajaRepository, IPedidoRepository pedidoRepository,
                             IPersonaRepository personaRepository, IUnidadDeVentaRepository unidadRepository) {
        this.cierreCajaRepository = cierreCajaRepository;
        this.pedidoRepository = pedidoRepository;
        this.personaRepository = personaRepository;
        this.unidadRepository = unidadRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esCajero(String username) {
        return buscarCajero(username) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CierreCajaDTO> resumenDeHoy(String username) {
        Cajero cajero = buscarCajero(username);
        if (cajero == null) {
            return List.of();
        }
        LocalDate hoy = LocalDate.now();
        return unidadRepository.findActivasByStaff(cajero.getIdPersona()).stream()
                .filter(u -> festivalEnCurso(u.getFestival(), hoy))
                .map(u -> cierreCajaRepository.findByUnidadVenta_IdUnidadDeVentaAndFecha(u.getIdUnidadDeVenta(), hoy)
                        .map(this::toDTO)                      // ya cerrada: se muestra el cierre guardado
                        .orElseGet(() -> resumenAbierto(u, hoy))) // abierta: se calcula con los pedidos de hoy
                .toList();
    }

    @Override
    @Transactional
    public CierreCajaDTO cerrar(String username, int idUnidad) {
        LocalDate hoy = LocalDate.now();

        // Validación de carga 1: solo un cajero cierra caja
        Cajero cajero = buscarCajero(username);
        if (cajero == null) {
            throw new IllegalArgumentException("Solo un cajero puede cerrar la caja.");
        }
        // Validación de carga 2: la unidad tiene que ser una donde trabaja
        UnidadDeVenta unidad = unidadRepository.findActivasByStaff(cajero.getIdPersona()).stream()
                .filter(u -> u.getIdUnidadDeVenta() == idUnidad)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No trabaja en esa unidad de venta."));
        // Validación de carga 3: el festival de la unidad tiene que estar en curso
        if (!festivalEnCurso(unidad.getFestival(), hoy)) {
            throw new IllegalArgumentException("Solo se puede cerrar la caja durante un festival en curso.");
        }
        // Validación de carga 4: un solo cierre por unidad y por día
        if (cierreCajaRepository.existsByUnidadVenta_IdUnidadDeVentaAndFecha(idUnidad, hoy)) {
            throw new IllegalArgumentException("La caja de " + unidad.getNombreComercial() + " ya se cerró hoy.");
        }

        int cantidad = (int) pedidoRepository.cantidadPedidosDelDia(idUnidad, hoy);
        double monto = pedidoRepository.recaudacionDelDia(idUnidad, hoy);
        CierreCaja cierre = new CierreCaja(hoy, unidad, unidad.getFestival(), cajero, cantidad, monto);
        return toDTO(cierreCajaRepository.save(cierre));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CierreCajaDTO> historial(String username) {
        Cajero cajero = buscarCajero(username);
        if (cajero == null) {
            return List.of();
        }
        List<Integer> misUnidades = unidadRepository.findActivasByStaff(cajero.getIdPersona()).stream()
                .map(UnidadDeVenta::getIdUnidadDeVenta).toList();
        if (misUnidades.isEmpty()) {
            return List.of();
        }
        return cierreCajaRepository.findByUnidadVenta_IdUnidadDeVentaInOrderByFechaDesc(misUnidades)
                .stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaCerrada(int idUnidad, LocalDate fecha) {
        return cierreCajaRepository.existsByUnidadVenta_IdUnidadDeVentaAndFecha(idUnidad, fecha);
    }

    /** Cajero dueño del usuario logueado, o null si no es una persona o no es cajero. */
    private Cajero buscarCajero(String username) {
        return personaRepository.findByCredencial_Username(username)
                .filter(p -> p instanceof Cajero)
                .map(p -> (Cajero) p)
                .orElse(null);
    }

    private boolean festivalEnCurso(Festival f, LocalDate dia) {
        return f != null && !dia.isBefore(f.getFechaInicio()) && !dia.isAfter(f.getFechaFin());
    }

    /** Caja todavía abierta: los totales se calculan con los pedidos del día. */
    private CierreCajaDTO resumenAbierto(UnidadDeVenta u, LocalDate dia) {
        CierreCajaDTO dto = new CierreCajaDTO();
        dto.setFecha(dia);
        dto.setUnidadId(u.getIdUnidadDeVenta());
        dto.setUnidadNombre(u.getNombreComercial());
        dto.setUnidadCodigo(u.getCodigo());
        dto.setFestivalNombre(u.getFestival() != null ? u.getFestival().getNombre() : "-");
        dto.setCantidadPedidos(pedidoRepository.cantidadPedidosDelDia(u.getIdUnidadDeVenta(), dia));
        dto.setMontoTotal(pedidoRepository.recaudacionDelDia(u.getIdUnidadDeVenta(), dia));
        dto.setCerrada(false);
        return dto;
    }

    /** Cierre ya guardado. */
    private CierreCajaDTO toDTO(CierreCaja c) {
        CierreCajaDTO dto = new CierreCajaDTO();
        dto.setIdCierreCaja(c.getIdCierreCaja());
        dto.setFecha(c.getFecha());
        dto.setUnidadId(c.getUnidadVenta().getIdUnidadDeVenta());
        dto.setUnidadNombre(c.getUnidadVenta().getNombreComercial());
        dto.setUnidadCodigo(c.getUnidadVenta().getCodigo());
        dto.setFestivalNombre(c.getFestival().getNombre());
        dto.setCajeroNombre(c.getCajero().getApellido() + ", " + c.getCajero().getNombre());
        dto.setCantidadPedidos(c.getCantidadPedidos());
        dto.setMontoTotal(c.getMontoTotal());
        dto.setCerrada(true);
        return dto;
    }
}