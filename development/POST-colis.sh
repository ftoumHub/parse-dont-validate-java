#!/bin/bash

curl -X POST --location "http://localhost:8080/api/v2/colis" \
    -H "Content-Type: application/json" \
    -d "{
          \"email\": \"georgesginon@gmail.com\",
          \"adresse\": {
            \"type\": \"AdresseBtoC\",
            \"ligne1\": \"48 rue de Brioux\",
            \"ligne4\": \"79000\",
            \"ligne6\": \"NIORT\"
          }
        }"