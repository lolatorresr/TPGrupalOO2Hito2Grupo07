package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.dtos.PlatoDTO;
import com.unla.TPGrupal002Hito2Grupo07.entities.Plato;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IFestivalRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPedidoRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPlatoRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IUnidadDeVentaRepository;
import com.unla.TPGrupal002Hito2Grupo07.services.IPlatoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("platoService")
public class PlatoService implements IPlatoService {

    @Autowired
    @Qualifier("platoRepository")
    private IPlatoRepository platoRepository;

    @Autowired
    @Qualifier("pedidoRepository")
    private IPedidoRepository pedidoRepository;

    @Autowired
    @Qualifier("festivalRepository")
    private IFestivalRepository festivalRepository;

    @Autowired
    @Qualifier("unidadDeVentaRepository")
    private IUnidadDeVentaRepository unidadRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<Plato> getAll(){
        return platoRepository.findAll();
    }

    @Override
    public PlatoDTO insertOrUpdate(PlatoDTO platoDTO){
        Plato plato = platoRepository.save(modelMapper.map(platoDTO, Plato.class));
        return modelMapper.map(plato, PlatoDTO.class);
    }

    @Override
    public PlatoDTO findById(int id){
        Plato plato = platoRepository.findById(id).orElse(null);
        return (plato != null) ? modelMapper.map(plato, PlatoDTO.class) : null;
    }

    @Override
    public boolean remove(int id){
        try{
            platoRepository.deleteById(id);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    @Override
    public PlatoDTO obtenerPlatoEstrella(int idFestival, int idUnidad) {
        List<Plato> resultados = pedidoRepository.findPlatoEstrella(idFestival, idUnidad, PageRequest.of(0,1));
        if(resultados != null && !resultados.isEmpty()){
            Plato plato = resultados.get(0);
            PlatoDTO dto = modelMapper.map(plato, PlatoDTO.class);
            // El DTO del reporte lleva el festival, la unidad y la cantidad vendida del plato
            festivalRepository.findById(idFestival).ifPresent(f -> dto.setFestival(f.getNombre()));
            unidadRepository.findById(idUnidad).ifPresent(u -> dto.setUnidadDeVenta(u.getNombreComercial() + " (" + u.getCodigo() + ")"));
            dto.setCantidad((int) pedidoRepository.cantidadVendida(idFestival, idUnidad, plato.getIdPlato()));
            return dto;
        }
        return null;
    }
}
