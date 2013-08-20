package fr.kissy.modules.maven.heroku.plugin;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;

@Mojo(name = "deploy", requiresProject = true)
public final class DeployMojo extends AbstractMojo {
    @Component
    private BuildPluginManager buildPluginManager;

    @Parameter(property = "project", readonly = true)
    private MavenProject mavenProject;
    @Parameter(property = "session", readonly = true)
    private MavenSession mavenSession;
    @Parameter(property = "settings", readonly = true)
    private Settings settings;
    @Parameter(property = "project.build.directory", readonly = true)
    private String projectBuildDirectory;
    @Parameter(property = "project.build.finalName", readonly = true)
    private String projectBuildFinalName;

    @Parameter(defaultValue = "heroku")
    private String server;
    @Parameter(required = true)
    private String application;
    @Parameter(required = true)
    private String procfile;

    private Git git;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoFailureException {
        final File checkoutDirectory = FileSystems.getDefault().getPath(mavenProject.getBuild().getDirectory(), "heroku").toFile();

        addSshKey();
        deleteOldRepository(checkoutDirectory);
        checkoutRepository(checkoutDirectory);
        retrieveJettyRunnerLib(checkoutDirectory);
        copyWarFile(checkoutDirectory);
        copyHerokuFiles(checkoutDirectory);
        commitAndPush();
    }

    /**
     * Get credentials provider.
     *
     * @throws MojoFailureException If somethings goes wrong
     */
    private void addSshKey() throws MojoFailureException {
        final Server serverSettings = settings.getServer(server);
        if (serverSettings == null) {
            throw new MojoFailureException("Server '" + server + "' not found in settings.xml");
        }

        final String privateKey = serverSettings.getPrivateKey();
        if (privateKey == null || privateKey.isEmpty()) {
            throw new MojoFailureException("PrivateKey for '\" + server + \"' not found in settings.xml");
        }

        final File privateKeyFile = new File(privateKey);
        if (!privateKeyFile.exists()) {
            throw new MojoFailureException("PrivateKey '" + privateKeyFile + "' doesn't exist");
        }

        JSch.setConfig("StrictHostKeyChecking", "no");
        JSch jsch = new JSch();
        try {
            jsch.addIdentity(privateKeyFile.getAbsolutePath());
        } catch (JSchException e) {
            throw new MojoFailureException("Unable to add the private key '" + privateKeyFile + "'");
        }
    }

    private void deleteOldRepository(File checkoutDirectory) throws MojoFailureException {
        try {
            if (checkoutDirectory.exists()) {
                FileUtils.deleteDirectory(checkoutDirectory);
            }
        } catch (IOException e) {
            throw new MojoFailureException("Error while deleting old repository " + checkoutDirectory.getAbsolutePath());
        }
    }

    private void checkoutRepository(File checkoutDirectory) throws MojoFailureException {
        try {
            git = Git.cloneRepository().setDirectory(checkoutDirectory).setBranch("master")
                    .setURI("git@heroku.com:" + application + ".git").call();
        } catch (Exception e) {
            throw new MojoFailureException("Error while cloning the repository " + application);
        }
    }

    private void retrieveJettyRunnerLib(File checkoutDirectory) throws MojoFailureException {
        try {
            MojoExecutor.executeMojo(
                    mavenProject.getPlugin("org.apache.maven.plugins:maven-dependency-plugin"),
                    MojoExecutor.goal("copy"),
                    MojoExecutor.configuration(
                            MojoExecutor.element(MojoExecutor.name("artifactItems"),
                                    MojoExecutor.element(MojoExecutor.name("artifactItem"),
                                            MojoExecutor.element(MojoExecutor.name("groupId"), "org.eclipse.jetty"),
                                            MojoExecutor.element(MojoExecutor.name("artifactId"), "jetty-runner"),
                                            MojoExecutor.element(MojoExecutor.name("version"), "9.0.4.v20130625"),
                                            MojoExecutor.element(MojoExecutor.name("destFileName"), "jetty-runner.jar")
                                    )
                            )
                    ),
                    MojoExecutor.executionEnvironment(mavenProject, mavenSession, buildPluginManager)
            );
            File jettyRunnerFile = FileSystems.getDefault().getPath(projectBuildDirectory, "dependency", "jetty-runner.jar").toFile();
            File outputFile = FileSystems.getDefault().getPath(checkoutDirectory.getAbsolutePath(), "jetty-runner.jar").toFile();
            if (outputFile.exists()) {
                FileUtils.forceDelete(outputFile);
            }
            FileUtils.copyFile(jettyRunnerFile, outputFile);
        } catch (Exception e) {
            throw new MojoFailureException("Failed to retrieve the jetty-runner.jar library");
        }
    }

    private void copyWarFile(File checkoutDirectory) throws MojoFailureException {
        try {
            File warFile = FileSystems.getDefault().getPath(projectBuildDirectory, projectBuildFinalName + ".war").toFile();
            File outputFile = FileSystems.getDefault().getPath(checkoutDirectory.getAbsolutePath(), projectBuildFinalName + ".war").toFile();
            if (outputFile.exists()) {
                FileUtils.forceDelete(outputFile);
            }
            FileUtils.copyFile(warFile, outputFile);
        } catch (Exception e) {
            throw new MojoFailureException("Failed to copy the war file");
        }
    }

    private void copyHerokuFiles(File checkoutDirectory) throws MojoFailureException {
        File herokuDirectory = FileSystems.getDefault().getPath(mavenProject.getBasedir().getAbsolutePath(), "heroku").toFile();
        try {
            FileUtils.copyDirectory(herokuDirectory, checkoutDirectory);
        } catch (Exception e) {
            throw new MojoFailureException("Error while copying Heroku files, " + herokuDirectory.getAbsolutePath() + " not found");
        }
    }

    private void commitAndPush() throws MojoFailureException {
        try {
            git.commit().setAll(true).setAuthor("Heroku Maven Plugin", "heroku@maven-plugin.com")
                    .setCommitter("Heroku Maven Plugin", "heroku@maven-plugin.com").setMessage("Updating Heroku").call();
            git.push().call();
        } catch (Exception e) {
            throw new MojoFailureException("Error while committing & pushing to Heroku");
        }
    }
}
