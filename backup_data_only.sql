--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: approver; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.approver (approver_id, digital_signature, email, name, password, role) FROM stdin;
1	newSignature123	john.smith@example.com	John Updated	newPassword123	Manager
\.


--
-- Data for Name: committee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.committee (id, committee_name, contact_email, head_of_committee, password) FROM stdin;
2	Updated Organizing Committee	updated@college.com	Ravi Sharma	newpass123
1	Tech Committee	techcommittee@gmail.com	Priya Mehta	admin123
\.


--
-- Data for Name: venue; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.venue (venue_id, available, capacity, facilities, venue_location, venue_name) FROM stdin;
1	t	200	Projector, Sound System, Stage	Main Block	Auditorium A
\.


--
-- Data for Name: event; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.event (event_id, created_date, event_date, event_name, expected_participants, status, committee_id, venue_id) FROM stdin;
7	2025-09-28	2025-10-15	TechFest	300	Ongoing	1	1
8	2025-10-21	2025-11-16	TechHack	100	Upcoming	1	1
\.


--
-- Data for Name: permission_application; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.permission_application (application_id, permission_doc, status, upload_date, event_id) FROM stdin;
1	https://example.com/uploads/permission_letter.pdf	Approved	2025-10-08	7
2	https://example.com/uploads/permission_letter2.pdf	Approved	2025-10-09	7
\.


--
-- Data for Name: approval; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.approval (approval_id, approval_date, approval_status, remarks, approver_id, application_id) FROM stdin;
1	2025-10-08	Approved	All safety and compliance checks passed	1	1
2	2025-10-10	Approved	Approved after submitting clearance document	1	2
\.


--
-- Data for Name: booking; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.booking (booking_id, booking_date, event_date, event_description, event_name, status, time_slot, venue_location, venue_name, end_time, start_time, event_id, venue_id) FROM stdin;
1	2025-10-08	2025-10-15	Annual tech conference	TechFest	Scheduled	\N	Main Block	Auditorium A	12:00:00	10:00:00	7	1
4	2025-10-08	2025-11-10	Annual tech conference	TechHack	Scheduled	\N	Main Block	Auditorium A	12:00:00	10:00:00	8	1
5	2025-10-08	2025-11-12	Annual tech conference	TechHack	Scheduled	\N	Main Block	Auditorium A	12:00:00	10:00:00	8	1
6	2025-10-08	2025-10-12	Annual tech conference	TechHack	Scheduled	\N	Main Block	Auditorium A	12:00:00	10:00:00	8	1
\.


--
-- Name: approval_approval_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.approval_approval_id_seq', 3, true);


--
-- Name: approver_approver_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.approver_approver_id_seq', 2, true);


--
-- Name: booking_booking_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.booking_booking_id_seq', 6, true);


--
-- Name: committee_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.committee_id_seq', 3, true);


--
-- Name: event_event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.event_event_id_seq', 8, true);


--
-- Name: permission_application_application_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.permission_application_application_id_seq', 2, true);


--
-- Name: venue_venue_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.venue_venue_id_seq', 1, true);


--
-- PostgreSQL database dump complete
--

