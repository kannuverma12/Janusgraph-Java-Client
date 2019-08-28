(/var/www/monstache/monstache -tpl -f  /var/www/monstache/schoolConfig_DirectRead.toml -worker A >> /var/www/monstache/logs/service.log &) &&
(/var/www/monstache/monstache -tpl -f  /var/www/monstache/schoolConfig_DirectRead.toml -worker B >> /var/www/monstache/logs/service.log &) &&
(/var/www/monstache/monstache -tpl -f  /var/www/monstache/schoolConfig_DirectRead.toml -worker C >> /var/www/monstache/logs/service.log &) &&
(/var/www/monstache/monstache -tpl -f  /var/www/monstache/schoolConfig_DirectRead.toml -worker D >> /var/www/monstache/logs/service.log )
