PGDMP                         x            covid_19    9.6.17    12.2     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �           1262    65536    covid_19    DATABASE     z   CREATE DATABASE covid_19 WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_GB.UTF-8' LC_CTYPE = 'en_GB.UTF-8';
    DROP DATABASE covid_19;
                jet    false            	            2615    65570    covid_19_schema    SCHEMA        CREATE SCHEMA covid_19_schema;
    DROP SCHEMA covid_19_schema;
                jet    false            �            1259    99525    api_call_history    TABLE     �   CREATE TABLE covid_19_schema.api_call_history (
    id bigint NOT NULL,
    api_call_type character varying(255),
    date timestamp without time zone,
    status character varying(255)
);
 -   DROP TABLE covid_19_schema.api_call_history;
       covid_19_schema            jet    false    9            �            1259    99523    api_call_history_id_seq    SEQUENCE     �   CREATE SEQUENCE covid_19_schema.api_call_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 7   DROP SEQUENCE covid_19_schema.api_call_history_id_seq;
       covid_19_schema          jet    false    9    198            �           0    0    api_call_history_id_seq    SEQUENCE OWNED BY     e   ALTER SEQUENCE covid_19_schema.api_call_history_id_seq OWNED BY covid_19_schema.api_call_history.id;
          covid_19_schema          jet    false    197            �            1259    98405    cases_imported    TABLE     �  CREATE TABLE covid_19_schema.cases_imported (
    id integer NOT NULL,
    province character varying,
    city character varying,
    citycode character varying,
    countrycode character varying,
    deaths integer,
    country character varying,
    lon double precision,
    confirmed integer,
    date date,
    active integer,
    lat double precision,
    recovered integer
);
 +   DROP TABLE covid_19_schema.cases_imported;
       covid_19_schema            postgres    false    9            �            1259    98403    cases_imported_id_seq    SEQUENCE     �   CREATE SEQUENCE covid_19_schema.cases_imported_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 5   DROP SEQUENCE covid_19_schema.cases_imported_id_seq;
       covid_19_schema          postgres    false    9    196            �           0    0    cases_imported_id_seq    SEQUENCE OWNED BY     a   ALTER SEQUENCE covid_19_schema.cases_imported_id_seq OWNED BY covid_19_schema.cases_imported.id;
          covid_19_schema          postgres    false    195            �            1259    99536    country    TABLE       CREATE TABLE covid_19_schema.country (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    common_name character varying(255),
    iso character varying(255),
    name character varying(255)
);
 $   DROP TABLE covid_19_schema.country;
       covid_19_schema            jet    false    9            �            1259    99534    country_id_seq    SEQUENCE     �   CREATE SEQUENCE covid_19_schema.country_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE covid_19_schema.country_id_seq;
       covid_19_schema          jet    false    200    9            �           0    0    country_id_seq    SEQUENCE OWNED BY     S   ALTER SEQUENCE covid_19_schema.country_id_seq OWNED BY covid_19_schema.country.id;
          covid_19_schema          jet    false    199            �            1259    99582    global_cases    TABLE     �  CREATE TABLE covid_19_schema.global_cases (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    case_date timestamp without time zone,
    is_global boolean NOT NULL,
    new_confirmed bigint,
    new_deaths bigint,
    new_recovered bigint,
    total_confirmed bigint,
    total_deaths bigint,
    total_recovered bigint,
    country_id integer NOT NULL
);
 )   DROP TABLE covid_19_schema.global_cases;
       covid_19_schema            jet    false    9            �            1259    99580    global_cases_country_id_seq    SEQUENCE     �   CREATE SEQUENCE covid_19_schema.global_cases_country_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 ;   DROP SEQUENCE covid_19_schema.global_cases_country_id_seq;
       covid_19_schema          jet    false    9    203            �           0    0    global_cases_country_id_seq    SEQUENCE OWNED BY     m   ALTER SEQUENCE covid_19_schema.global_cases_country_id_seq OWNED BY covid_19_schema.global_cases.country_id;
          covid_19_schema          jet    false    202            �            1259    99578    global_cases_id_seq    SEQUENCE     �   CREATE SEQUENCE covid_19_schema.global_cases_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 3   DROP SEQUENCE covid_19_schema.global_cases_id_seq;
       covid_19_schema          jet    false    9    203            �           0    0    global_cases_id_seq    SEQUENCE OWNED BY     ]   ALTER SEQUENCE covid_19_schema.global_cases_id_seq OWNED BY covid_19_schema.global_cases.id;
          covid_19_schema          jet    false    201                       2604    99528    api_call_history id    DEFAULT     �   ALTER TABLE ONLY covid_19_schema.api_call_history ALTER COLUMN id SET DEFAULT nextval('covid_19_schema.api_call_history_id_seq'::regclass);
 K   ALTER TABLE covid_19_schema.api_call_history ALTER COLUMN id DROP DEFAULT;
       covid_19_schema          jet    false    197    198    198                       2604    98408    cases_imported id    DEFAULT     �   ALTER TABLE ONLY covid_19_schema.cases_imported ALTER COLUMN id SET DEFAULT nextval('covid_19_schema.cases_imported_id_seq'::regclass);
 I   ALTER TABLE covid_19_schema.cases_imported ALTER COLUMN id DROP DEFAULT;
       covid_19_schema          postgres    false    196    195    196                       2604    99539 
   country id    DEFAULT     z   ALTER TABLE ONLY covid_19_schema.country ALTER COLUMN id SET DEFAULT nextval('covid_19_schema.country_id_seq'::regclass);
 B   ALTER TABLE covid_19_schema.country ALTER COLUMN id DROP DEFAULT;
       covid_19_schema          jet    false    200    199    200                       2604    99585    global_cases id    DEFAULT     �   ALTER TABLE ONLY covid_19_schema.global_cases ALTER COLUMN id SET DEFAULT nextval('covid_19_schema.global_cases_id_seq'::regclass);
 G   ALTER TABLE covid_19_schema.global_cases ALTER COLUMN id DROP DEFAULT;
       covid_19_schema          jet    false    201    203    203                       2604    99586    global_cases country_id    DEFAULT     �   ALTER TABLE ONLY covid_19_schema.global_cases ALTER COLUMN country_id SET DEFAULT nextval('covid_19_schema.global_cases_country_id_seq'::regclass);
 O   ALTER TABLE covid_19_schema.global_cases ALTER COLUMN country_id DROP DEFAULT;
       covid_19_schema          jet    false    203    202    203            
           2606    99533 &   api_call_history api_call_history_pkey 
   CONSTRAINT     m   ALTER TABLE ONLY covid_19_schema.api_call_history
    ADD CONSTRAINT api_call_history_pkey PRIMARY KEY (id);
 Y   ALTER TABLE ONLY covid_19_schema.api_call_history DROP CONSTRAINT api_call_history_pkey;
       covid_19_schema            jet    false    198                       2606    99544    country country_pkey 
   CONSTRAINT     [   ALTER TABLE ONLY covid_19_schema.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (id);
 G   ALTER TABLE ONLY covid_19_schema.country DROP CONSTRAINT country_pkey;
       covid_19_schema            jet    false    200                       2606    99588    global_cases global_cases_pkey 
   CONSTRAINT     e   ALTER TABLE ONLY covid_19_schema.global_cases
    ADD CONSTRAINT global_cases_pkey PRIMARY KEY (id);
 Q   ALTER TABLE ONLY covid_19_schema.global_cases DROP CONSTRAINT global_cases_pkey;
       covid_19_schema            jet    false    203                       2606    99589 (   global_cases fkpwqmsumps35xed5jpf9ga152v    FK CONSTRAINT     �   ALTER TABLE ONLY covid_19_schema.global_cases
    ADD CONSTRAINT fkpwqmsumps35xed5jpf9ga152v FOREIGN KEY (country_id) REFERENCES covid_19_schema.country(id) ON DELETE CASCADE;
 [   ALTER TABLE ONLY covid_19_schema.global_cases DROP CONSTRAINT fkpwqmsumps35xed5jpf9ga152v;
       covid_19_schema          jet    false    203    200    2060           