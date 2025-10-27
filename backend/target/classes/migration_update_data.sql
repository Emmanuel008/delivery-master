-- Simple migration to update existing data and modify status column
-- Update existing records to have a default status first
UPDATE outbox_sms SET status = 'Pending' WHERE status LIKE 'HTTP 200%';
UPDATE outbox_sms SET status = 'Failed' WHERE status LIKE 'HTTP 4%' OR status LIKE 'HTTP 5%';
UPDATE outbox_sms SET status = 'Error' WHERE status LIKE 'ERROR%';

-- Modify status column to be shorter (for status values like Pending, Delivered, Failed, etc.)
ALTER TABLE outbox_sms 
MODIFY COLUMN status VARCHAR(50) NOT NULL;

-- Create index on message_id for faster lookups
CREATE INDEX idx_outbox_sms_message_id ON outbox_sms(message_id);

-- Extract messageId from existing response data where possible
UPDATE outbox_sms 
SET message_id = SUBSTRING(response, 
    LOCATE('"shootId":"', response) + 11,
    LOCATE('"', response, LOCATE('"shootId":"', response) + 11) - LOCATE('"shootId":"', response) - 11
)
WHERE response LIKE '%"shootId":"%' AND message_id IS NULL;
