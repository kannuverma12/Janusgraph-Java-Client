(/var/www/monstache/monstache -tpl -f /var/www/monstache/paytmSourceDataConfig.toml -worker A >> /var/www/monstache/logs/service.log &) &&
(/var/www/monstache/monstache -tpl -f /var/www/monstache/paytmSourceDataConfig.toml -worker B >> /var/www/monstache/logs/service.log &) &&
(/var/www/monstache/monstache -tpl -f /var/www/monstache/paytmSourceDataConfig.toml -worker C >> /var/www/monstache/logs/service.log &) &&
(/var/www/monstache/monstache -tpl -f /var/www/monstache/paytmSourceDataConfig.toml -worker D >> /var/www/monstache/logs/service.log )
