import api from './index';

export interface Notification {
  id: string;
  userId: string;
  type: 'READING_REMINDER' | 'GOAL_ACHIEVED' | 'FRIEND_ACTIVITY';
  message: string;
  read: boolean;
  createdAt: string;
}

const notificationService = {
  getNotifications: async (): Promise<Notification[]> => {
    const response = await api.get('/notifications');
    return response.data;
  },

  markAsRead: async (notificationId: string): Promise<void> => {
    await api.put(`/notifications/${notificationId}/read`);
  },

  getUnreadCount: async (): Promise<number> => {
    const response = await api.get('/notifications/unread/count');
    return response.data;
  },

  updatePreferences: async (preferences: { 
    enableEmailNotifications: boolean,
    enablePushNotifications: boolean,
    readingReminders: boolean
  }): Promise<void> => {
    await api.put('/notifications/preferences', preferences);
  },
};

export default notificationService;
