CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS telemetry_aggregates (
    id BIGSERIAL PRIMARY KEY,
    patient_id VARCHAR(64) NOT NULL,
    window_end TIMESTAMPTZ NOT NULL,
    summary_text TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS daily_evaluations (
    id BIGSERIAL PRIMARY KEY,
    patient_id VARCHAR(64) NOT NULL,
    eval_date DATE NOT NULL,
    summary_text TEXT NOT NULL,
    embedding_vector vector(4) NOT NULL,
    CONSTRAINT uq_daily_eval UNIQUE (patient_id, eval_date)
);

CREATE INDEX IF NOT EXISTS idx_daily_eval_vector ON daily_evaluations USING ivfflat (embedding_vector vector_cosine_ops);
