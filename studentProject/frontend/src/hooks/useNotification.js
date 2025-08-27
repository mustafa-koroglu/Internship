// Bildirim sistemi için custom hook
import { useState, useCallback } from "react";

export const useNotification = () => {
  const [message, setMessage] = useState("");
  const [type, setType] = useState(""); // 'success', 'error', 'info'

  const showMessage = useCallback((msg, msgType = "info") => {
    setMessage(msg);
    setType(msgType);
    setTimeout(() => {
      setMessage("");
      setType("");
    }, 3000);
  }, []);

  const showSuccess = useCallback(
    (msg) => {
      showMessage(msg, "success");
    },
    [showMessage]
  );

  const showError = useCallback(
    (msg) => {
      showMessage(msg, "error");
    },
    [showMessage]
  );

  return {
    message,
    type,
    setMessage,
    showMessage,
    showSuccess,
    showError,
  };
};
