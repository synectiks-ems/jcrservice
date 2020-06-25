CREATE TABLE cloud_provider_config
(
   id bigint PRIMARY KEY NOT NULL,
   provider varchar(255),
   access_key varchar(2000),
   secrate_key varchar(2000),
   bucket varchar(2000),
   end_point varchar(2000),
   permission_mode varchar(255),
   permission_mode_value varchar(255)
)
;

CREATE TABLE cloud_context_path
(
   id bigint PRIMARY KEY NOT NULL,
   provider varchar(255),
   plugin varchar(255),
   path varchar(2000)
)
;

CREATE TABLE documents
(
   id bigint PRIMARY KEY NOT NULL,
   file_name varchar(255),
   local_file_path varchar(500),
   cloud_file_path varchar(2000),
   client_type varchar(20),
   client_id bigint,
   cloud_context_path_id bigint,
   cdn varchar(255),
   status varchar(20)
)
;
ALTER TABLE documents
ADD CONSTRAINT fk_documents_cloud_context_path_id
FOREIGN KEY (cloud_context_path_id)
REFERENCES cloud_context_path(id)
;
create sequence hibernate_sequence MINVALUE 1 MAXVALUE 999999999999999999  START WITH 1 INCREMENT BY 1  NO CYCLE;