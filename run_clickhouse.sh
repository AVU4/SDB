docker run -d --name server-clickhouse --volume=/home/avu/IdeaProjects/sdb/output/result_data.tsv:/var/lib/clickhouse/result_data.tsv yandex/clickhouse-server