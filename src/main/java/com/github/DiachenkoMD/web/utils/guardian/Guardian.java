package com.github.DiachenkoMD.web.utils.guardian;

import com.github.DiachenkoMD.web.utils.guardian.guards.Guard;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

public class Guardian {

    private final static Logger logger = LogManager.getLogger(Guardian.class);

    private final HashMap<String, Guard> guards = new HashMap<>();

    public void init(Class<?>... classes) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(classes == null || classes.length == 0)
            return;

        this.init(Set.of(classes));
    }

    public void init(Set<Class<?>> classes) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<String> protectedRoutes = new LinkedList<>();

        for(Class<?> guarded : classes){
            UseGuards guardAnno = guarded.getAnnotation(UseGuards.class);

            Class<? extends Guard>[] connectedGuards = guardAnno.value();

            LinkedList<Guard> guardsList = new LinkedList<>();
            for(Class<? extends Guard> guardClass : connectedGuards){
                guardsList.add(guardClass.getConstructor().newInstance());
            }

            WebServlet webServletAnno = guarded.getAnnotation(WebServlet.class);

            String path = webServletAnno.value()[0];

            Guard pipelinedGuard = Guard.pipe(guardsList);

            guards.put(path, pipelinedGuard);

            protectedRoutes.add(path);
        }

        logger.info("Guardian will protect: {}", protectedRoutes);
    }

    public boolean guard(String path, HttpServletRequest req, HttpServletResponse resp){
        try {
            Guard relatedGuard = guards.get(path);

            if(relatedGuard != null)
                return relatedGuard.check(req, resp);

            return true;
        }catch (Exception e){
            logger.error(e);
            return false;
        }
    }
}
