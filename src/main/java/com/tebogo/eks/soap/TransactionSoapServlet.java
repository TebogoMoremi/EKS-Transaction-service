package com.tebogo.eks.soap;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import jakarta.servlet.ServletConfig;

public class TransactionSoapServlet extends CXFNonSpringServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void loadBus(ServletConfig servletConfig) {

        super.loadBus(servletConfig);

        Bus bus = getBus();

        BusFactory.setDefaultBus(bus);

        JaxWsServerFactoryBean factory =
                new JaxWsServerFactoryBean();

        factory.setBus(bus);

        factory.setServiceClass(
                TransactionSoapService.class
        );

        factory.setServiceBean(
                new TransactionSoapServiceImpl()
        );

        factory.setAddress(
                "/transactions"
        );

        factory.create();
    }
}