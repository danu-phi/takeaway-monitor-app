package com.phsmk.id.takeaway_monitor.util

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import timber.log.Timber

class NsdHelper(context: Context) {

    private val nsdManager: NsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null
    private var resolveListener: NsdManager.ResolveListener? = null

    var serviceName: String? = null

    fun registerService(port: Int, type: String = "_http._tcp", name: String = "TakeawayMonitor") {
        tearDownRegistration() // Cancel any previous registration

        registrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
                serviceName = NsdServiceInfo.serviceName
                Timber.d("Service registered: ${NsdServiceInfo.serviceName}")
            }

            override fun onRegistrationFailed(arg0: NsdServiceInfo, arg1: Int) {
                Timber.e("Service registration failed: $arg1")
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                Timber.d("Service unregistered: ${arg0.serviceName}")
            }

            override fun onUnregistrationFailed(arg0: NsdServiceInfo, arg1: Int) {
                Timber.e("Service unregistration failed: $arg1")
            }
        }

        val serviceInfo = NsdServiceInfo().apply {
            this.serviceName = name
            this.serviceType = type
            this.port = port
        }

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    fun discoverServices(type: String = "_http._tcp", onServiceResolved: (NsdServiceInfo) -> Unit) {
        stopDiscovery()

        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {
                Timber.d("Service discovery started")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                Timber.d("Service found: ${service.serviceName}")
                if (service.serviceType != type) {
                    Timber.d("Unknown Service Type: ${service.serviceType}")
                } else if (service.serviceName.contains("CloudPOS Master")) {
                    // This is our own service or a similar one
                    nsdManager.resolveService(service, createResolveListener(onServiceResolved))
                } else {
                    // General resolve for other services if needed
                    nsdManager.resolveService(service, createResolveListener(onServiceResolved))
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                Timber.e("Service lost: $service")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Timber.i("Discovery stopped: $serviceType")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Timber.e("Discovery failed: Error code: $errorCode")
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Timber.e("Discovery stop failed: Error code: $errorCode")
                nsdManager.stopServiceDiscovery(this)
            }
        }

        nsdManager.discoverServices(type, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    private fun createResolveListener(onServiceResolved: (NsdServiceInfo) -> Unit): NsdManager.ResolveListener {
        return object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Timber.e("Resolve failed: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Timber.d("Resolve Succeeded. $serviceInfo")
                if (serviceInfo.serviceName == serviceName) {
                    Timber.d("Same machine: $serviceName")
                    return
                }
                onServiceResolved(serviceInfo)
            }
        }
    }

    fun stopDiscovery() {
        discoveryListener?.let {
            nsdManager.stopServiceDiscovery(it)
            discoveryListener = null
        }
    }

    fun tearDownRegistration() {
        registrationListener?.let {
            nsdManager.unregisterService(it)
            registrationListener = null
        }
    }
}
