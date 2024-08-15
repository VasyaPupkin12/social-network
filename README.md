# social-network
OTUS. Домашнее задание

Для запуска проекта использовать docker-compose.yml. Внутри проекта есть коллекция postman для проверки работы

### Для работы со slave postgres:
1. Сделаем бэкап для реплик: 
```bash
docker exec -it social-network-pgmaster-1 bash
mkdir /pgslave
pg_basebackup -h pgmaster -D /pgslave -U replicator -v -P --wal-method=stream
exit
```
2. Копируем директорию себе
```bash
docker cp pgmaster:/pgslave volumes/pgslave/
```
3. Создадим файл, чтобы реплика узнала, что она реплика
```bash
touch volumes/pgslave/standby.signal
```
4. Меняем **postgresql.conf** на реплике **pgslave**
```bash
primary_conninfo = 'host=social-network-pgmaster-1 port=5432 user=replicator password=postgres application_name=pgslave'
```
5. Расскоментировать строчки в **docker-compose.yml**
```yaml
  pgslave:
    image: postgres
    ports:
      - "15432:5432"
    volumes:
      - ./volumes/pgslave:/var/lib/postgresql/data
    environment:
      - POSTGRES_PASSWORD=postgres
      - POSTGRES_USER=postgres
    depends_on:
      - pgmaster
   networks:
     - pgnet
```
### Для масштабирования RABBIT MQ и WEBSOCKET
1. Для добавления еще одного экземпляра RABBIT MQ:
```yaml
  rabbitmq: #изменить название
      image: arm64v8/rabbitmq:3-management
      ports:
          - "15672:15672" #изменить номер порта для подключения
          - "5672:5672" #изменить номер порта для подключения
      environment:
          - RABBITMQ_DEFAULT_USER=admin
          - RABBITMQ_DEFAULT_PASS=admin
      networks:
          - pgnet
```
2. Для добавления еще одного экземпляра WEBSOCKET:
```yaml
    social-network: #новое название экземпляра
    build:
        context: .
        args:
            - no-cache
    ports:
        - "8080:8080"
    depends_on:
        - pgmaster
        - pgslave
        - tarantool
        - rabbitmq
    networks:
        - pgnet
    environment:
        - DB_NAME=social_network
        - DB_M_SERVER=pgmaster
        - DB_M_PORT=5432
        - DB_M_USER=postgres
        - DB_M_PASS=postgres
        - DB_S_SERVER=pgslave
        - DB_S_PORT=5432
        - DB_S_USER=postgres
        - DB_S_PASS=postgres
        - DB_SH_SERVER=master
        - DB_SH_PORT=5432
        - DB_SH_USER=postgres
        - DB_SH_PASS=postgres
        - TARANTOOL=tarantool
        - TARANTOOL_PORT=3301
        - TARANTOOL_PORT=3301
        - TARANTOOL_USER=guest
        - TARANTOOL_PASS=
        - RABBIT_MQ=rabbitmq #подставить название rabbit mq из шага 2
        - RABBIT_PORT=5672 #подставить номер порта rabbit mq из шага 2
        - RABBIT_USER=admin
        - RABBIT_PASSWORD=admin
```