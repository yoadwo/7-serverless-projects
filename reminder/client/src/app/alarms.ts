export interface AlarmGet {
  userId: string;
  Message: string;
  ExpireOn: Date;
}

export interface Alarms {
  message: AlarmGet[];
}
