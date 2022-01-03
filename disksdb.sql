--
-- PostgreSQL database dump
--

-- Dumped from database version 14.1
-- Dumped by pg_dump version 14.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: adminpack; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS adminpack WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION adminpack; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION adminpack IS 'administrative functions for PostgreSQL';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: audio_disk; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.audio_disk (
    disk_id integer NOT NULL,
    disk_type character varying(30) NOT NULL,
    audios_on_disk text NOT NULL
);


ALTER TABLE public.audio_disk OWNER TO postgres;

--
-- Name: disk; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.disk (
    disk_id integer NOT NULL,
    title character varying(50) NOT NULL,
    place character varying(20) NOT NULL,
    media_type character varying(10),
    publisher_id integer,
    scan_url text,
    cost real,
    comment text
);


ALTER TABLE public.disk OWNER TO postgres;

--
-- Name: document_disk; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.document_disk (
    disk_id integer NOT NULL,
    doc_on_disk text NOT NULL
);


ALTER TABLE public.document_disk OWNER TO postgres;

--
-- Name: film_disk; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.film_disk (
    disk_id integer NOT NULL,
    genre character varying(20)
);


ALTER TABLE public.film_disk OWNER TO postgres;

--
-- Name: publisher; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.publisher (
    publisher_id integer NOT NULL,
    name text
);


ALTER TABLE public.publisher OWNER TO postgres;

--
-- Name: seq_next_id; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_next_id
    START WITH 9
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_next_id OWNER TO postgres;

--
-- Name: seq_next_id; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.seq_next_id OWNED BY public.disk.disk_id;


--
-- Name: software_disk; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.software_disk (
    disk_id integer NOT NULL,
    soft_on_disk text NOT NULL
);


ALTER TABLE public.software_disk OWNER TO postgres;

--
-- Name: disk disk_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.disk ALTER COLUMN disk_id SET DEFAULT nextval('public.seq_next_id'::regclass);


--
-- Data for Name: audio_disk; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.audio_disk (disk_id, disk_type, audios_on_disk) FROM stdin;
1	Аудиокнига	Аудиокнига
2	Аудиокнига	Аудиокнига по Программированию
3	Сборник Аудиокниг	Все стихи пушкина
4	mp3	Альбом группы Rammstein
\.


--
-- Data for Name: disk; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.disk (disk_id, title, place, media_type, publisher_id, scan_url, cost, comment) FROM stdin;
2	Исскуство Программирования	На полке	\N	\N	\N	\N	\N
3	Сборник Стихов Пушкина	В проигрывателе	\N	\N	\N	\N	\N
4	Альбом Группы Rammstein	На полке	\N	\N	\N	\N	\N
5	Финансовая отчётность за 1 квартал	В сейфе	\N	\N	\N	\N	\N
6	Все мои пароли	В сейфе	\N	\N	\N	\N	\N
9	Виды Ярославля	На столе	\N	\N	\N	\N	\N
7	Microsoft Access	В компьютере	\N	\N	\N	\N	\N
1	Приключения Гарри Поттера	У Друга	CD	4	\N	499.99	Диск с аудиокнигой о похождениях Гарри Поттера
8	Windows 11	На полке	CD-ROM	5	\N	5000	Любимая винда
10	Терминатор	В проигрывателе	CD-DVD	3	\N	500	Диск с фильмом Терминатор
\.


--
-- Data for Name: document_disk; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.document_disk (disk_id, doc_on_disk) FROM stdin;
5	Финансовая отчётность за 1 квартал 2021 года
6	Все пароли, используемые мной
9	Диск с фотографиями Ярославля
\.


--
-- Data for Name: film_disk; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.film_disk (disk_id, genre) FROM stdin;
10	Боевик
\.


--
-- Data for Name: publisher; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.publisher (publisher_id, name) FROM stdin;
1	MegaDisks
2	Music Company
3	Universal Media
4	Santa Claus Corp
5	Microsoft
\.


--
-- Data for Name: software_disk; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.software_disk (disk_id, soft_on_disk) FROM stdin;
7	Диск с переносной версией ПО Microsoft Access
8	Диск образом Windows 11
\.


--
-- Name: seq_next_id; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_next_id', 10, true);


--
-- Name: disk pk_disk_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.disk
    ADD CONSTRAINT pk_disk_id PRIMARY KEY (disk_id);


--
-- Name: publisher pk_publisher_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.publisher
    ADD CONSTRAINT pk_publisher_id PRIMARY KEY (publisher_id);


--
-- Name: audio_disk audio_disk_fk_disk_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.audio_disk
    ADD CONSTRAINT audio_disk_fk_disk_id_fkey FOREIGN KEY (disk_id) REFERENCES public.disk(disk_id);


--
-- Name: disk disk_publisher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.disk
    ADD CONSTRAINT disk_publisher_id_fkey FOREIGN KEY (publisher_id) REFERENCES public.publisher(publisher_id);


--
-- Name: document_disk document_disk_fk_disk_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.document_disk
    ADD CONSTRAINT document_disk_fk_disk_id_fkey FOREIGN KEY (disk_id) REFERENCES public.disk(disk_id);


--
-- Name: film_disk film_disk_disk_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.film_disk
    ADD CONSTRAINT film_disk_disk_id_fkey FOREIGN KEY (disk_id) REFERENCES public.disk(disk_id);


--
-- Name: software_disk software_disk_fk_disk_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.software_disk
    ADD CONSTRAINT software_disk_fk_disk_id_fkey FOREIGN KEY (disk_id) REFERENCES public.disk(disk_id);


--
-- PostgreSQL database dump complete
--

