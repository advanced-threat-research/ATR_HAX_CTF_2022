rm -rf hidden_services
unzip hidden_services.zip
(cd internal && sudo docker build --tag tor:1.0 .)
sudo docker run -p 80:80 -p 1337:1337 --detach --name tor tor:1.0
sudo docker exec -it tor ps ax
sudo docker exec -it tor service nginx start
sudo docker exec -it tor service tor start
