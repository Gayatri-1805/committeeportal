-- Migration: Add NOT NULL constraint to approver_id in permission_application table

-- First, update any NULL values with the first available approver
UPDATE public.permission_application
SET approver_id = 1
WHERE approver_id IS NULL;

-- Then add NOT NULL constraint
ALTER TABLE public.permission_application
ALTER COLUMN approver_id SET NOT NULL;

-- Verify the change
SELECT column_name, is_nullable, data_type 
FROM information_schema.columns 
WHERE table_name = 'permission_application' AND column_name = 'approver_id';
