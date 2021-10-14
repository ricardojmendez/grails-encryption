dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
    dbCreate = "create-drop"
    url = "jdbc:h2:~/test/testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
}
hibernate {
    cache.queries = false
    cache.use_second_level_cache = false
    cache.use_query_cache = false
}

