-- https://platform.stratascratch.com/coding/2056-number-of-shipments-per-month?code_type=1

select EXTRACT(year from shipment_date) || '-' || EXTRACT(month from shipment_date) AS year_month, COUNT(*) AS shipment_count
FROM amazon_shipment
GROUP BY year_month;