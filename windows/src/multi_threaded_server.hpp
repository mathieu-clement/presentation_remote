#pragma once
#ifndef MULTI_THREADED_SERVER_HPP
#define MULTI_THREADED_SERVER_HPP

#include <QThread>
#include <QObject>

class WorkerThread : public QThread
{
    Q_OBJECT

    void run() Q_DECL_OVERRIDE;
};

#endif

