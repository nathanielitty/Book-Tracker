import * as React from "react"

// Simple toast hook for now
export function useToast() {
  const toast = React.useCallback(({ title, description }: { title?: string; description?: string }) => {
    console.log('Toast:', title, description);
    // Simple alert for now - replace with proper toast implementation later
    if (title) alert(title);
  }, []);

  return { toast };
}