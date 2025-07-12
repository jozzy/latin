package org.latin.server.functions

import org.eclipse.lmos.arc.agents.dsl.FunctionDefinitionContext
import org.eclipse.lmos.arc.agents.dsl.extensions.info
import org.latin.server.modules.ModuleExecutor
import java.util.concurrent.ConcurrentHashMap

fun FunctionDefinitionContext.buildEmailFunctions(
    moduleExecutor: ModuleExecutor,
    eventListeners: ConcurrentHashMap<String, suspend (String) -> String>,
) {
    function(
        name = "read_email",
        description = "Reads an email from the inbox",
    ) {
        info("Reading latest email")
        """
           Email Id: AQ1234
           Email from: John Doe
           Subject: CUSTOMER COMPLAINT 
           Content:
           Dear Mr.Smith,
           
           We received your COMPLAINT and have processed it successfully.
           
           Best regards,
           The Support Team
        """
    }

    function(
        name = "mark_important",
        description = "Marks an email as important",
        params = types(
            string("id", "The ID of the email to mark as important"),
        ),
    ) { (id) ->
        info("Marking email $id as important")
        """
         Email with ID $id has been marked as important.
        """
    }
}
