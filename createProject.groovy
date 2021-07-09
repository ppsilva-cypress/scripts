import com.atlassian.jira.bc.project.ProjectCreationData
import com.atlassian.jira.bc.project.ProjectService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.AssigneeTypes
import com.atlassian.jira.project.type.ProjectTypeKey
import com.atlassian.jira.issue.CustomFieldManager

def customFieldManager = ComponentAccessor.getCustomFieldManager()

//my project data
//Project key
def CFchave = customFieldManager.getCustomFieldObject("customfield_11773")
def valorChave = issue.getCustomFieldValue(CFchave)
//Project Name
def CFNome = customFieldManager.getCustomFieldObject("customfield_11772")
def valorNome = issue.getCustomFieldValue(CFNome)
//Project Lead
def lider = issue.getReporter()
//admin user
def userManager = ComponentAccessor.getUserManager()
def admin = userManager.getUserByName("jiraadmin")
//Project Type Key
def projectTypeKey = new ProjectTypeKey("software")


// bulding project creation data
def projectService = ComponentAccessor.getComponent(ProjectService)

def projectCreationData = new ProjectCreationData.Builder().with {
    withKey(valorChave)
    withName(valorNome)
    withLead(lider)
    withAssigneeType(AssigneeTypes.UNASSIGNED)
    withType(projectTypeKey)
    withProjectTemplateKey("com.pyxis.greenhopper.jira:gh-scrum-template") //Project Template Key
}.build()

//validating project creation data
ProjectService.CreateProjectValidationResult createProjectValidationResult =
        projectService.validateCreateProject(
                admin,
                projectCreationData)

if(!createProjectValidationResult.getErrorCollection().errors)
{
    projectService.createProject(createProjectValidationResult) //creating project
    def CFLink = customFieldManager.getCustomFieldObject("customfield_11774")
	issue.setCustomFieldValue(CFLink, "https://jiracorp.petrobras.com.br/projects/" + valorChave + "/issues")
    issue.setSummary("Criando Projeto " + valorNome)
    
} else {
    def CFErro = customFieldManager.getCustomFieldObject("customfield_11775")
	issue.setCustomFieldValue(CFErro, "Project cannot be created ${createProjectValidationResult.getErrorCollection().errors}")
    
}
