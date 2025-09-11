INSERT INTO application_status (name, description)
VALUES ('PENDING_REVIEW', 'Pending status'),
       ('REJECTED', 'Rejected status'),
       ('MANUAL_REVIEW', 'Manual Review status'),
       ('COMPLETED', 'Completed status');

INSERT INTO loan_type (name, min_amount, max_amount, interest_rate, automatic_validation)
VALUES ('Basic', 1000.00, 3000.00, 1.50, true),
       ('Plus', 4000.00, 5000.00, 1.60, false);