package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.unla.TPGrupal002Hito2Grupo07.dtos.PlatoDTO;
import com.unla.TPGrupal002Hito2Grupo07.entities.Plato;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPedidoRepository;
import com.unla.TPGrupal002Hito2Grupo07.repositories.IPlatoRepository;
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
            return modelMapper.map(resultados.get(0), PlatoDTO.class);
        }
        return null;
    }
}
