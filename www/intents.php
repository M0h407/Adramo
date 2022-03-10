<?php
    //header('Access-Control-Allow-Origin: *');
    
    $result = $_POST['result2'];
    echo json_encode($result);

    $xml1=simplexml_load_file('envios.xml');
    $i=-1;
        foreach($xml1->children() as $envio){
            $i=$i+1;
            $id_envios = $envio->id_envios;
            $intents =$envio->intents;

            if($id_envio== $result )
            {
                $envio->intents = intents + 1;
            }
        }
        file_put_contents("envios.xml",$xml1->saveXML());

?>