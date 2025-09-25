INSERT INTO application_status (name, description)
VALUES ('PENDING_REVIEW', 'Pending status'),
       ('REJECTED', 'Rejected status'),
       ('MANUAL_REVIEW', 'Manual Review status'),
       ('APPROVED', 'Completed status');

INSERT INTO loan_type (name, min_amount, max_amount, interest_rate, automatic_validation)
VALUES ('Basic', 100000.00, 300000.00, 1.50, true),
       ('Plus', 400000.00, 500000.00, 1.60, false);