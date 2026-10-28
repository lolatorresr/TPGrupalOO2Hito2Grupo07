package com.unla.TPGrupal002Hito2Grupo07.helpers;

public class ViewRouteHelper {
	/**** Views ****/
	//HOME
	public final static String INDEX = "home/index";
	public final static String HELLO = "home/hello";

	//DEGREE
	public final static String DEGREE_INDEX = "degree/index";
	public final static String DEGREE_FORM = "degree/form";
	public final static String DEGREE_NEW = "degree/new";

	//PERSON
	public final static String PERSON_INDEX = "person/index";
	public final static String PERSON_NEW = "person/new";
	public final static String PERSON_UPDATE = "person/update";
	public final static String PERSON_PARTIAL_VIEW = "person/partialPersonView";

	//USER
	public final static String USER_LOGIN = "user/login";
	public final static String USER_LOGOUT = "user/logout";

	//PLATO
	public final static String PLATO_INDEX = "plato/index";

	//PLATO ESTRELLA
	public static final String PLATO_ESTRELLA_INDEX = "plato/estrella";

	//UNIDAD DE VENTA
	public final static String UNIDAD_INDEX = "unidad/index";
	public final static String UNIDAD_FORM = "unidad/form";

	//FESTIVAL
	public final static String FESTIVAL_DETALLE = "festival/detalle";

	//EMPLEADO
	public final static String EMPLEADO_INICIO = "empleado/inicio";
	public final static String EMPLEADO_LIQUIDACION = "empleado/liquidacion";

	//REGISTRO DE RESPONSABLE CON UNIDAD DE VENTA
	public final static String REGISTRO_FORM = "registro/form";

	//REPORTE
	public final static String REPORTE_INDEX = "reporte/index";
	public final static String REPORTE_RANKING = "reporte/ranking";
	public final static String REPORTE_CANON = "reporte/canon";
	public final static String REPORTE_RECAUDACION = "reporte/recaudacion";
	public final static String REPORTE_RENTABILIDAD = "reporte/rentabilidad";

	//PEDIDO
	public final static String PEDIDO_INDEX = "pedido/index";
	public final static String PEDIDO_FORM = "pedido/form";


	/**** Redirects ****/
	public final static String ROUTE = "/index";
	public final static String DEGREE_ROOT = "/degrees/";
	public final static String PERSON_ROOT = "/person";
	public final static String PLATO_ROOT = "/platos/";
	public final static String UNIDAD_ROOT = "/unidades/";
	public final static String EMPLEADO_ROOT = "/empleado";
	public final static String LIQUIDACION_ROOT = "/liquidacion";
	public final static String FESTIVAL_ROOT = "/festivales/";

	public final static String REGISTRO_ROOT = "/registro";
	public final static String PEDIDO_ROOT = "/pedidos/";
	public final static String REPORTE_ROOT = "/reportes/";
}
