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