pytest test_shipping.py --cov=shipping
pytest test_shipping.py::test_standard_returned_for_large_non_priority_order --cov=shipping
pytest test_shipping.py::test_standard_returned_for_large_non_priority_order --cov=shipping --cov-report=term-missing
pytest test_shipping.py::test_standard_returned_for_large_non_priority_order --cov=shipping --cov-report=html