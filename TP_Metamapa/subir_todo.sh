#!/bin/bash
USER="ramidisanto"

echo "Construyendo y subiendo Frontend..."
docker build -t $USER/frontend:latest ./FrontEnd
docker push $USER/frontend:latest

echo "Construyendo y subiendo Microservicios..."
docker build -t $USER/moduloagregador:latest ./ModuloAgregador
docker push $USER/moduloagregador:latest

docker build -t $USER/moduloavd:latest ./ModuloAVD
docker push $USER/moduloavd:latest

docker build -t $USER/modulodinamica:latest ./ModuloDinamica
docker push $USER/modulodinamica:latest

docker build -t $USER/moduloestadisticas:latest ./ModuloEstadisticas
docker push $USER/moduloestadisticas:latest

docker build -t $USER/moduloestatica:latest ./ModuloEstatica
docker push $USER/moduloestatica:latest

docker build -t $USER/modulonormalizador:latest ./ModuloNormalizador
docker push $USER/modulonormalizador:latest

docker build -t $USER/moduloproxy:latest ./ModuloProxy
docker push $USER/moduloproxy:latest

docker build -t $USER/modulopublico:latest ./ModuloPublico
docker push $USER/modulopublico:latest

docker build -t $USER/moduloauth:latest ./ModuloAuth
docker push $USER/moduloauth:latest

echo "Imagenes subidas a la nube."