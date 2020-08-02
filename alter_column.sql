ALTER TABLE covid_19_schema.global_cases ALTER COLUMN case_date TYPE date USING (case_date::date);
ALTER TABLE covid_19_schema.global_cases ALTER COLUMN case_time TYPE time USING (case_time::time);