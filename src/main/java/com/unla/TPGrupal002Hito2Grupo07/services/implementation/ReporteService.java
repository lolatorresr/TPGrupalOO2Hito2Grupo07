package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.dtos.ReporteMayoresCanonDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.ReporteRankingUnidadDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.ReporteRentabilidadDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.ReporteSueldoDTO;
import com.unla.TPGrupal002Hito2Grupo07.dtos.ReporteVentaDTO;
import com.unla.TPGrupal002Hito2Grupo07.entities.Cajero;
import com.unla.TPGrupal002Hito2Grupo07.entities.Cocinero;
import com.unla.TPGrupal002Hito2Grupo07.entities.Festival;
import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;
import com.unla.TPGrupal002Hito2Grupo07.entities.UnidadDeVenta;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IFestivalRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPedidoRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IUnidadDeVentaRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.ILiquidacionService;
import com.unla.TPGrupal002Hito2Grupo07.services.IReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("reporteService")
public class ReporteService implements IReporteService {

    @Autowired
    @Qualifier("unidadDeVentaRepository")
    private IUnidadDeVentaRepository unidadRepository;

    @Autowired
    @Qualifier("pedidoRepository")
    private IPedidoRepository pedidoRepository;

    @Autowired
    @Qualifier("festivalRepository")
    private IFestivalRepository festivalRepository;

    @Autowired
    @Qualifier("liquidacionService")
    private ILiquidacionService liquidacionService;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteRankingUnidadDTO> rankingPorRecaudacion(int idFestival) {
        // Recaudación y cantidad de pedidos por unidad, calculadas en la base
        Map<Integer, Object[]> totales = new HashMap<>();
        for (Object[] fila : pedidoRepository.recaudacionPorUnidad(idFestival)) {
            totales.put((Integer) fila[0], fila);
        }
        // Se parte de las unidades activas del festival para que figuren también las que no vendieron
        List<UnidadDeVenta> unidades = new ArrayList<>(unidadRepository.findByFestival_IdFestivalAndActivoTrue(idFestival));
        unidades.sort(Comparator.comparingDouble((UnidadDeVenta u) -> recaudacionDe(totales, u)).reversed()
                .thenComparing(UnidadDeVenta::getNombreComercial));

        List<ReporteRankingUnidadDTO> ranking = new ArrayList<>();
        int posicion = 1;
        for (UnidadDeVenta u : unidades) {
            Object[] fila = totales.get(u.getIdUnidadDeVenta());
            long pedidos = fila != null ? ((Number) fila[2]).longValue() : 0;
            String responsable = u.getResponsable() != null
                    ? u.getResponsable().getNombre() + " " + u.getResponsable().getApellido() : "-";
            ranking.add(new ReporteRankingUnidadDTO(posicion++, u.getIdUnidadDeVenta(), u.getNombreComercial(),
                    u.getCodigo(), u.getTipo(), responsable, pedidos, recaudacionDe(totales, u)));
        }
        return ranking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteMayoresCanonDTO> mayoresCanon(int idFestival, int cantidad) {
        List<UnidadDeVenta> unidades = new ArrayList<>(unidadRepository.findByFestival_IdFestivalAndActivoTrue(idFestival));
        // El canon lo calcula cada unidad según su tipo (calcularCanon)
        unidades.sort(Comparator.comparingDouble(UnidadDeVenta::calcularCanon).reversed()
                .thenComparing(UnidadDeVenta::getNombreComercial));
        List<ReporteMayoresCanonDTO> reporte = new ArrayList<>();
        int posicion = 1;
        for (UnidadDeVenta u : unidades.subList(0, Math.min(cantidad, unidades.size()))) {
            reporte.add(new ReporteMayoresCanonDTO(posicion++, u.getNombreComercial(), u.getCodigo(),
                    u.getTipo(), u.calcularCanon()));
        }
        return reporte;
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteVentaDTO recaudacionFestival(int idFestival, Integer idResponsable) {
        Festival festival = festivalRepository.findById(idFestival).orElse(null);
        if (festival == null) {
            return null;
        }
        // Unidades que abarca el reporte: las del responsable, o todas las del festival para el administrador
        List<UnidadDeVenta> unidades = idResponsable != null
                ? unidadRepository.findByFestival_IdFestivalAndResponsable_IdPersonaAndActivoTrue(idFestival, idResponsable)
                : unidadRepository.findByFestival_IdFestivalAndActivoTrue(idFestival);
        Map<Integer, Object[]> totales = new HashMap<>();
        for (Object[] fila : pedidoRepository.recaudacionPorUnidad(idFestival)) {
            totales.put((Integer) fila[0], fila);
        }
        unidades = new ArrayList<>(unidades);
        unidades.sort(Comparator.comparingDouble((UnidadDeVenta u) -> recaudacionDe(totales, u)).reversed()
                .thenComparing(UnidadDeVenta::getNombreComercial));

        List<ReporteRankingUnidadDTO> detalle = new ArrayList<>();
        long pedidos = 0;
        double total = 0;
        int posicion = 1;
        for (UnidadDeVenta u : unidades) {
            Object[] fila = totales.get(u.getIdUnidadDeVenta());
            long pedidosUnidad = fila != null ? ((Number) fila[2]).longValue() : 0;
            double recaudacion = recaudacionDe(totales, u);
            String responsable = u.getResponsable() != null
                    ? u.getResponsable().getNombre() + " " + u.getResponsable().getApellido() : "-";
            detalle.add(new ReporteRankingUnidadDTO(posicion++, u.getIdUnidadDeVenta(), u.getNombreComercial(),
                    u.getCodigo(), u.getTipo(), responsable, pedidosUnidad, recaudacion));
            pedidos += pedidosUnidad;
            total += recaudacion;
        }
        return new ReporteVentaDTO(festival.getIdFestival(), festival.getNombre(), festival.getTemporada(),
                festival.getFechaInicio(), festival.getFechaFin(), unidades.size(), pedidos, total, detalle);
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteRentabilidadDTO rentabilidadNeta(int idFestival, int idUnidad) {
        return rentabilidadNetaEntreFechas(idFestival, idUnidad, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteRentabilidadDTO rentabilidadNetaEntreFechas(int idFestival, int idUnidad, LocalDate desde, LocalDate hasta) {
        UnidadDeVenta unidad = unidadRepository.findById(idUnidad).orElse(null);
        if (unidad == null || unidad.getFestival() == null || unidad.getFestival().getIdFestival() != idFestival) {
            return null;
        }
        Festival festival = unidad.getFestival();
        // El período se acota al festival; sin fechas se toma el festival completo
        LocalDate inicio = desde == null || desde.isBefore(festival.getFechaInicio()) ? festival.getFechaInicio() : desde;
        LocalDate fin = hasta == null || hasta.isAfter(festival.getFechaFin()) ? festival.getFechaFin() : hasta;
        long diasFestival = ChronoUnit.DAYS.between(festival.getFechaInicio(), festival.getFechaFin()) + 1;
        long diasPeriodo = Math.max(0, ChronoUnit.DAYS.between(inicio, fin) + 1);
        // Sueldos y canon son del festival entero: en un período menor se toman en proporción a los días
        double proporcion = diasFestival > 0 ? (double) diasPeriodo / diasFestival : 1;

        // Ventas y costo de los platos vendidos en el período (calculados en la base)
        Object[] totales = pedidoRepository.ventasYCostos(idFestival, idUnidad, inicio, fin).get(0);
        double ventas = ((Number) totales[0]).doubleValue();
        double costoPlatos = ((Number) totales[1]).doubleValue();
        long pedidos = ((Number) totales[2]).longValue();

        // Sueldos de los empleados del staff (cajeros y cocineros) según la liquidación de haberes
        List<ReporteSueldoDTO> detalleSueldos = new ArrayList<>();
        double sueldos = 0;
        for (Persona p : unidad.getStaff()) {
            if (p instanceof Cajero || p instanceof Cocinero) {
                double sueldo = liquidacionService.calcularSueldoTotal(p.getIdPersona(), idFestival) * proporcion;
                String puesto = p instanceof Cajero ? "Cajero"
                        : ((Cocinero) p).getCategoria() != null ? ((Cocinero) p).getCategoria().getDescripcion() : "Cocinero";
                detalleSueldos.add(new ReporteSueldoDTO(p.getApellido() + ", " + p.getNombre(), puesto, sueldo));
                sueldos += sueldo;
            }
        }
        detalleSueldos.sort(Comparator.comparing(ReporteSueldoDTO::getEmpleado));
        double canon = unidad.calcularCanon() * proporcion;

        return new ReporteRentabilidadDTO(festival.getNombre(), unidad.getNombreComercial(), unidad.getCodigo(),
                unidad.getTipo(), inicio, fin, diasFestival, diasPeriodo, pedidos, ventas, costoPlatos, sueldos, canon,
                ventas - costoPlatos - sueldos - canon, detalleSueldos);
    }

    private double recaudacionDe(Map<Integer, Object[]> totales, UnidadDeVenta u) {
        Object[] fila = totales.get(u.getIdUnidadDeVenta());
        return fila != null ? ((Number) fila[1]).doubleValue() : 0;
    }
}
