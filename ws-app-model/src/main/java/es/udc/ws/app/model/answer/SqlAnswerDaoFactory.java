package es.udc.ws.app.model.answer;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class SqlAnswerDaoFactory {

    private final static String CLASS_NAME_PARAMETER = "SqlAnswerDaoFactory.className";
    private static SqlAnswerDao dao = null;

    private SqlAnswerDaoFactory(){

    }

    @SuppressWarnings("rawtypes")
    private static SqlAnswerDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlAnswerDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static SqlAnswerDao getDao() {

        if (dao == null) {
            dao = getInstance();
        }
        return dao;
    }
}
