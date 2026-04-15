-- Migration: Add approver_id column to permission_application table

-- Add the approver_id column to permission_application
ALTER TABLE public.permission_application
ADD COLUMN approver_id bigint;

-- Populate approver_id from approval table by matching application_id
UPDATE public.permission_application
SET approver_id = (
    SELECT DISTINCT a.approver_id 
    FROM public.approval a 
    WHERE a.application_id = public.permission_application.application_id
    LIMIT 1
)
WHERE EXISTS (
    SELECT 1 FROM public.approval a 
    WHERE a.application_id = public.permission_application.application_id
);

-- Add foreign key constraint
ALTER TABLE public.permission_application
ADD CONSTRAINT fk_permission_application_approver
FOREIGN KEY (approver_id) REFERENCES public.approver(approver_id);

-- Verify the changes
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'permission_application' 
ORDER BY ordinal_position;
