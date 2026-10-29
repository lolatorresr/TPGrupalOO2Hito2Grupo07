// Fechas: se muestran como dd/mm/aaaa (se puede escribir o elegir del calendario)
// y se envían al servidor como aaaa-mm-dd, que es el formato que espera el DTO.
// Mes y año se reemplazan por desplegables propios (flatpickr trae el mes como <select> nativo
// y el año como campo numérico con flechas). Los dos se arman con la misma función.
// opciones: lista de {valor, texto}; alElegir(valor) se llama al hacer click en una opción;
// esValida(valor), opcional, indica qué opciones se pueden elegir (las demás se ven en gris).
function crearDesplegable(opciones, alElegir, esValida) {
    var envoltorio = document.createElement('span');
    envoltorio.className = 'flatpickr-desplegable';
    var boton = document.createElement('span');
    boton.className = 'flatpickr-desplegable-actual';
    var lista = document.createElement('div');
    lista.className = 'flatpickr-desplegable-lista';
    opciones.forEach(function (op) {
        var opcion = document.createElement('div');
        opcion.className = 'flatpickr-desplegable-opcion';
        opcion.textContent = op.texto;
        opcion.dataset.valor = op.valor;
        opcion.addEventListener('click', function (e) {
            e.stopPropagation();
            lista.style.display = 'none';
            alElegir(parseInt(this.dataset.valor, 10));
        });
        lista.appendChild(opcion);
    });
    var desplegable = { envoltorio: envoltorio, lista: lista, valor: null };
    desplegable.mostrar = function (valor) {
        desplegable.valor = valor;
        var op = lista.querySelector('[data-valor="' + valor + '"]');
        boton.textContent = op ? op.textContent : valor;
        if (esValida) {
            lista.querySelectorAll('.flatpickr-desplegable-opcion').forEach(function (o) {
                o.classList.toggle('deshabilitado', !esValida(parseInt(o.dataset.valor, 10)));
            });
        }
    };
    desplegable.cerrar = function () { lista.style.display = 'none'; };
    boton.addEventListener('click', function (e) {
        e.stopPropagation();
        if (lista.style.display === 'block') {
            lista.style.display = 'none';
            return;
        }
        // Se cierra cualquier otra lista abierta del mismo calendario
        document.querySelectorAll('.flatpickr-desplegable-lista').forEach(function (l) { l.style.display = 'none'; });
        lista.style.display = 'block';
        // Se resalta la opción actual y se centra en la lista
        lista.querySelectorAll('.seleccionado').forEach(function (o) { o.classList.remove('seleccionado'); });
        var sel = lista.querySelector('[data-valor="' + desplegable.valor + '"]');
        if (sel) {
            sel.classList.add('seleccionado');
            lista.scrollTop = sel.offsetTop - lista.clientHeight / 2 + sel.offsetHeight / 2;
        }
    });
    document.addEventListener('click', desplegable.cerrar);
    envoltorio.appendChild(boton);
    envoltorio.appendChild(lista);
    return desplegable;
}
function mesYAnioComoDesplegables(instancia) {
    // Los años y meses que se ofrecen respetan la fecha mínima y máxima del campo: con máximo "hoy"
    // (nacimiento) no hay años futuros; en un rango acotado (fechas de un festival) solo esos años.
    // Sin límites se ofrecen 100 años para atrás y 5 para adelante.
    var minimo = instancia.config.minDate;
    var maximo = instancia.config.maxDate;
    var actual = new Date().getFullYear();
    var primerAnio = minimo ? minimo.getFullYear() : actual - 100;
    var ultimoAnio = maximo ? maximo.getFullYear() : actual + 5;
    // Mes: siempre los 12 (el select nativo ocultaba los posteriores a la fecha máxima)
    var meses = instancia.l10n.months.longhand.map(function (nombre, i) { return { valor: i, texto: nombre }; });
    var mes = crearDesplegable(meses, function (m) { instancia.changeMonth(m, false); }, function (m) {
        var antesDelMaximo = !maximo || instancia.currentYear < maximo.getFullYear() || m <= maximo.getMonth();
        var despuesDelMinimo = !minimo || instancia.currentYear > minimo.getFullYear() || m >= minimo.getMonth();
        return antesDelMaximo && despuesDelMinimo;
    });
    var anios = [];
    for (var a = ultimoAnio; a >= primerAnio; a--) anios.push({ valor: a, texto: String(a) });
    var anio = crearDesplegable(anios, function (a) { instancia.changeYear(a); });
    instancia.monthElements[0].replaceWith(mes.envoltorio);
    instancia.currentYearElement.parentNode.replaceWith(anio.envoltorio);
    instancia.actualizarCabecera = function () {
        mes.mostrar(instancia.currentMonth);
        anio.mostrar(instancia.currentYear);
    };
    instancia.cerrarListas = function () { mes.cerrar(); anio.cerrar(); };
    instancia.actualizarCabecera();
}
// Atributos del <input class="fecha">:
//   data-min / data-max: fecha límite (aaaa-mm-dd) o "hoy".
//   data-siguiente: id de otro campo de fecha que se abre solo al elegir esta (rangos desde/hasta);
//                   ese campo no admite fechas anteriores a la elegida.
function limite(valor) {
    return valor === 'hoy' ? 'today' : (valor || null);
}
document.querySelectorAll('input.fecha').forEach(function (input) {
    flatpickr(input, {
        locale: 'es',
        dateFormat: 'Y-m-d',
        altInput: true,
        altFormat: 'd/m/Y',
        allowInput: true,
        static: true,
        minDate: limite(input.dataset.min),
        maxDate: limite(input.dataset.max),
        onReady: function (fechas, valor, instancia) { mesYAnioComoDesplegables(instancia); },
        // Si se cambia de mes o año con las flechas, los desplegables se mantienen sincronizados
        onMonthChange: function (fechas, valor, instancia) { instancia.actualizarCabecera(); },
        onYearChange: function (fechas, valor, instancia) { instancia.actualizarCabecera(); },
        onClose: function (fechas, valor, instancia) {
            instancia.cerrarListas();
            var siguiente = input.dataset.siguiente && document.getElementById(input.dataset.siguiente);
            if (siguiente && siguiente._flatpickr && fechas.length) {
                siguiente._flatpickr.set('minDate', fechas[0]);
                siguiente._flatpickr.actualizarCabecera();
                // Se abre en el siguiente ciclo para que el cierre de este calendario no lo tape
                setTimeout(function () { siguiente._flatpickr.open(); }, 0);
            }
        }
    });
});

