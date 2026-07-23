class customException(Exception):
    def __init__(self, message):
        super().__init__(message)
    
raise customException("Custom exception bro!")