// window.addEventListener("load", inicio, false);
var app = {
  init:function(){
      navigator.geolocation.getCurrentPosition(app.onSucces,app.onError);
      var info_coord = document.getElementById("info_coord");
  },
 
  onSucces:function(position){

         map = L.map('zona_mapa').setView([position.coords.latitude, position.coords.longitude], 17);

         L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
             attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
         }).addTo(map);

          L.marker([position.coords.latitude, position.coords.longitude],{

            icon: L.icon({      
            iconUrl: "img/camion.png",
            iconSize: [70, 50]
        })
      }).bindPopup(
            "<h3>Estàs aqui</h3>"
            ).addTo(map)
          inicio();        
  },
  onError:function(error){
      info_coord.innerHTML = "<p>Error: "+ error.code + ". Missatge: "+ error.message + "</p>"
  }
}
  
function inicio() {
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function () {
      if (this.readyState == 4 && this.status == 200) {
          lecturaXML(this);
      }
  };
  xhttp.open("GET", "http://192.168.1.69/adramo/envios.xml", true);
  xhttp.send();

  if (navigator.geolocation) {
    navigator.geolocation.watchPosition(mostrarCoordenada);
  } else {
    alert('El navegador no disposa de geolocalización');
  }
}

function lecturaXML(xml) {
  var xmlDoc = xml.responseXML;

      for (var i = 0; i < xmlDoc.getElementsByTagName("envio").length; i++) {
          var latitud = xmlDoc.getElementsByTagName("latitud")[i].childNodes[0].nodeValue; 
          var longitud = xmlDoc.getElementsByTagName("longitud")[i].childNodes[0].nodeValue;
          idenvio = xmlDoc.getElementsByTagName("id_envios")[i].childNodes[0].nodeValue;
          idpaquet = xmlDoc.getElementsByTagName("id_paquet")[i].childNodes[0].nodeValue;
          dataenvio = xmlDoc.getElementsByTagName("data_envio")[i].childNodes[0].nodeValue;
          intents = xmlDoc.getElementsByTagName("intents")[i].childNodes[0].nodeValue;
          estat = xmlDoc.getElementsByTagName("estat")[i].childNodes[0].nodeValue;
          
          console.log(idenvio+estat);
          var popup2 = 
          '<div id = "popup">'+
          '<h2>Entrega</h2>'+
          '<p>'+'ID ENVIO: '+idenvio+'</p>'+
          '<p>'+'ID PAQUET: '+idpaquet+'</p>'+
          '<p>'+'DATA ENVIO: '+dataenvio+'</p>'+
          '<p>'+'INTENTS: '+intents+'</p>'+
          '<p>'+'ESTAT: '+estat+'</p>'+
          '<p id="entregatP"></p>'+
          '<div id = "popupimg">'+
          '<img id="Foto">'+
          '</div>'+
          '<div id = "botons">'+
          '<button id = "boto" onclick="cameraFoto()">CAMARA</button>'+
          '<button id = "boto" onclick="intent()">INTENT</button>'+
          '<button id = "boto" onclick="entregar()">ENTREGAR</button>'+
          '<button id = "boto" onclick="recargar()">RECARGAR</button>'+
          '</div>'+
          '</div>';

        if (estat == "true"){
            marker = new L.marker([latitud,longitud],{
                icon: L.icon({      
                iconUrl: "img/marker_entregat.png",
                iconSize: [30, 50]
            })
  
            }).bindPopup(popup2).addTo(map);
            marker.on('click', onClick);
        } else if (estat == "false" && intents == 0 || intents == 1 || intents == 2){
            marker = new L.marker([latitud,longitud],{
                icon: L.icon({      
                iconUrl: "img/marker_absent.png",
                iconSize: [30, 50]
            })
  
            }).bindPopup(popup2).addTo(map);
            marker.on('click', onClick);
        } else {
          marker = new L.marker([latitud,longitud],{
            icon: L.icon({      
              iconUrl: "img/marker_noentregat.png",
              iconSize: [30, 50]
            })

        }).bindPopup(popup2).addTo(map);
        marker.on('click', onClick);
        }
      }
}

function cameraFoto() { 
  navigator.camera.getPicture(onSuccess, onFail, {  
     quality: 100, 
     destinationType: Camera.DestinationType.DATA_URL ,
     correctOrientation: true
  });  
  
  function onSuccess(imageData) { 
      image = document.getElementById('Foto'); 
     image.src = "data:image/jpeg;base64," + imageData; 
  }  
  
  function onFail(message) { 
     alert('Failed because: ' + message); 
  } 
}

function entregar(){
  $.ajax({
    method: 'post',
    url: 'http://192.168.1.69/adramo/guardar.php',
  
    data: {result},
  
    success: function(response) {
      console.log(response);
    }
  });
}

function intent(){
  $.ajax({
    method: 'post',
    url: 'http://192.168.1.69/adramo/intent.php',
  
    data: {result},
  
    success: function(response) {
      console.log(response);
    }
  });
}

function onClick(e) {
  var popup = e.target.getPopup();
  var content = popup.getContent();

  console.log(content);
 result = content.substring(49, 48);
console.log(result);
}

function recargar(){
  location.reload();
}

function mostrarCoordenada(posicion) {
  document.getElementById('dato').innerHTML='Latitud:'+
     posicion.coords.latitude+
     '<br> Longitud:'+posicion.coords.longitude+
     '<br>Exactitud:'+posicion.coords.accuracy;
}

document.addEventListener('deviceready',app.init());