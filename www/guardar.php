<?php
    header('Access-Control-Allow-Origin: *');
    header("Allow: GET, POST, OPTIONS, PUT, DELETE");
    
    $result = $_POST['result'];
    echo json_encode($result);

    $xml1=simplexml_load_file('envios.xml');
    $i=-1;
        foreach($xml1->children() as $envio){
            $i=$i+1;
            $id_envios = $envio->id_envios;
            $estat =$envio->estat;

            if($id_envio== $result )
            {
                $envio->estat = "true";
            }
        }
        file_put_contents("envios.xml",$xml1->saveXML());

?>