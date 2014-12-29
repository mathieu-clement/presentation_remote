#pragma once
#ifndef TRAY_ICON_APP_HPP
#define TRAY_ICON_APP_HPP

#include <QObject>
#include <QSystemTrayIcon>
#include <QMenu>
#include <QThread>

class MyTrayIconAppObject : public QObject
{
    Q_OBJECT

    public:
        void setTrayIcon(QSystemTrayIcon* icon);

    public slots:
        void quitApplication();
        void iconActivated(QSystemTrayIcon::ActivationReason reason);

    private:
        QSystemTrayIcon* trayIcon;
}; // end of class


class WorkerThread : public QThread
{
    Q_OBJECT

    void run() Q_DECL_OVERRIDE;
};


QMenu* createContextMenu(MyTrayIconAppObject* obj);


#endif

