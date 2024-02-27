export interface AlarmGet {
  userId: string;
  message: string;
  expireOn: Date;
}

export interface Alarms {
  message: AlarmGet[];
}
