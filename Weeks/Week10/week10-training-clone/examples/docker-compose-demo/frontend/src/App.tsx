import { useEffect, useState } from 'react'

const API = "http://54.234.151.232:8080";

function App() {

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const fetchMessage = async () => {
    try{

      const response = await fetch(`${API}/api/hello`);
      const data = await response.text();
      setMessage(data);

    }catch(error){
      setError("Error");
    }
  }

  const checkHealth = async () => {
    try{
      const response = await fetch(`${API}/api/health`);
      return response.ok;
    }catch(error){
      return false;
    }
  }

  useEffect(() => {
    fetchMessage();
    // Check the health every 5 seconds
    const interval = setInterval(async () => {
      const isHealthy = await checkHealth();
      if(!isHealthy){
        setError("Backend is not responding");
      }
    }, 5000)

    return () => clearInterval(interval);
  }, []);

  return (
    <>
      <h1>Docker Compose Demo</h1>
      <h2>Backend Response</h2>   

      {
        message && <p>{message}</p>
      }
      <button onClick={fetchMessage}>Refresh Message</button>

      {
        error && <p>{error}</p>
      }
    </>
  )
}

export default App
