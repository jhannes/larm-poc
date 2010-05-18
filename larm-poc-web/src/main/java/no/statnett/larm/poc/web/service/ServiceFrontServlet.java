package no.statnett.larm.poc.web.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.web.service.LarmHessianSerializerFactory;
import no.statnett.larm.poc.client.stasjon.Stasjon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;

public class ServiceFrontServlet extends HttpServlet {

    private static final long serialVersionUID = -4263007495436844879L;

    private Map<String, HessianSkeleton> serviceMap = new HashMap<String, HessianSkeleton>();

    private Logger log = LoggerFactory.getLogger(getClass());


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HessianSkeleton service = serviceMap.get(getServiceName(req));
        if (service == null) {
            log.warn("Tried to access unknown service " + getServiceName(req));
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown service " + getServiceName(req));
            return;
        }

        try {
            SerializerFactory serializerFactory = new LarmHessianSerializerFactory();
            service.invoke(req.getInputStream(), resp.getOutputStream(), serializerFactory);
        } catch (InvocationTargetException e) {
            log.error("Error while processing request to " + getServiceName(req), e.getCause());
            if (e.getCause() instanceof RuntimeException) {
                throw ((RuntimeException)e.getCause());
            } else {
                throw new ServletException(e.getCause());
            }
        } catch (RuntimeException e) {
            log.error("Error while processing request to " + getServiceName(req), e);
            throw e;
        } catch (Exception e) {
            log.error("Error while processing request to " + getServiceName(req), e);
            throw new ServletException(e);
        }
    }

    private String getServiceName(HttpServletRequest req) {
        String serviceName = req.getPathInfo();
        return serviceName != null && serviceName.startsWith("/") ? serviceName.substring(1) : serviceName;
    }

    @Override
    public void init() throws ServletException {
        Repository repository = LarmHibernateRepository.withJndiUrl("jdbc/primaryDs");
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 1", "F01"));
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 2", "F01"));
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 3", "F02"));
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 4", "F03"));
        addService("repositoryService", Repository.class, repository);
    }

    private void addService(String serviceName, Class<Repository> serviceInterface, Repository serviceDelegate) {
        if (getServletContext().getAttribute(serviceName) != null) {
            serviceMap.put(serviceName, (HessianSkeleton) getServletContext().getAttribute(serviceName));
        } else {
            serviceMap.put(serviceName, new HessianSkeleton(serviceDelegate, serviceInterface));
        }
    }
}
