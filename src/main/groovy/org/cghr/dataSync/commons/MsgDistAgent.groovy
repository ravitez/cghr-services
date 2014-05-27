package org.cghr.dataSync.commons

import groovy.transform.CompileStatic
import org.cghr.dataSync.service.AgentService

@CompileStatic
class MsgDistAgent implements Agent {

    AgentService agentService

    MsgDistAgent(AgentService agentService) {
        this.agentService = agentService
    }

    public void run() {

        List distFiles = agentService.getInboxMessagesToDistribute()
        distributeMessages(distFiles)

    }

    void distributeMessages(List<Map<String,String>> messages) {

        messages.each {
            Map<String, String> message ->
                if (message.distList == null) return
                List<String> recepients = message.distList.split(",") as List
                recepients.each {
                    String recepient ->
                        agentService.distributeMessage(message, recepient)
                }
                agentService.distributeSuccessful(message);
        }

    }

}
