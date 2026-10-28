package com.unla.TPGrupal002Hito2Grupo07.controllers;

import com.unla.TPGrupal002Hito2Grupo07.entities.Festival;
import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;
import com.unla.TPGrupal002Hito2Grupo07.services.ILiquidacionService;
import com.unla.TPGrupal002Hito2Grupo07.services.implementation.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/liquidacion")
public class LiquidacionController {

    @Autowired
    @Qualifier("liquidacionService")
    private ILiquidacionService liquidacionService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping("")
    public ModelAndView verLiquidacion(
            @RequestParam(name = "idPersona", defaultValue = "1") int idPersona,
            @RequestParam(name = "idFestival", defaultValue = "1") int idFestival){

        ModelAndView mAV = new ModelAndView("empleado/liquidacion");

        Persona persona = liquidacionService.obtenerPersona(idPersona);
        Festival festival = liquidacionService.obtenerFestival(idFestival);
        double sueldoTotal = liquidacionService.calcularSueldoTotal(idPersona, idFestival);

        mAV.addObject("persona", persona);
        mAV.addObject("festival", festival);
        mAV.addObject("sueldoTotal", sueldoTotal);

        return mAV;
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf(
            @RequestParam(name = "idPersona", defaultValue = "1") int idPersona,
            @RequestParam(name = "idFestival", defaultValue = "1") int idFestival) {

        Persona persona = liquidacionService.obtenerPersona(idPersona);
        Festival festival = liquidacionService.obtenerFestival(idFestival);
        double sueldoTotal = liquidacionService.calcularSueldoTotal(idPersona, idFestival);

        byte[] pdfBytes = pdfGeneratorService.generarReciboSueldoPdf(persona, festival, sueldoTotal);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Recibo_Sueldo_" + idPersona + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
