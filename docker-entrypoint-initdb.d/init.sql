DO
$do$
BEGIN
   IF EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'docker') THEN

      RAISE NOTICE 'Role "docker" already exists. Skipping.';
   ELSE
      BEGIN   -- nested block
         CREATE ROLE docker LOGIN PASSWORD 'docker';
      EXCEPTION
         WHEN duplicate_object THEN
            RAISE NOTICE 'Role "docker" was just created by a concurrent transaction. Skipping.';
      END;
   END IF;
   
   GRANT CREATE ON SCHEMA public TO docker;
END
$do$;