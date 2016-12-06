# DB inserter
Tool for inserting huge amount of records in database. Use postgres database. Wrote with scala, akka and jdbc

# How to use
Clone this project to your repository, then start this command
```
sbt "run -u <db_user_name> -p <db_password> -a <threads count> -r <insert records count> -n <db_name> -m <table name> -c true"
```

Attention!! `-c true` will remove all records form table
