CREATE TABLE IF NOT EXISTS application_status (
                                    id BIGSERIAL PRIMARY KEY,
                                    name VARCHAR(100) NOT NULL,
                                    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS loan_type (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           min_amount NUMERIC(15,2) NOT NULL CHECK (min_amount >= 0),
                           max_amount NUMERIC(15,2) NOT NULL CHECK (max_amount >= min_amount),
                           interest_rate NUMERIC(5,2) NOT NULL CHECK (interest_rate >= 0),
                           automatic_validation BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS loan_application (
                                  id BIGSERIAL PRIMARY KEY,
                                  amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
                                  term_months INT NOT NULL CHECK (term_months > 0),
                                  email VARCHAR(150) NOT NULL,
                                  status_id BIGINT NOT NULL,
                                  loan_type_id BIGINT NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_status FOREIGN KEY (status_id) REFERENCES application_status(id),
                                  CONSTRAINT fk_loan_type FOREIGN KEY (loan_type_id) REFERENCES loan_type(id)
);
