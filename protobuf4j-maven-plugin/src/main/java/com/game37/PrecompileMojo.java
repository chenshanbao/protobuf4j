/**
 * 
 */
package com.game37;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.game37.protobuf4j.proto.utils.ClassSearchUtil;
import com.game37.protobuf4j.proto.utils.GlobalConfig;

/**
* author chenshanbao
*
**/
@Mojo(name = "precompile", defaultPhase = LifecyclePhase.COMPILE, threadSafe = true, 
					requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class PrecompileMojo extends AbstractMojo {
	
	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;
	
	@Parameter(required = true)
	private String scanPackageList;
	
	@Parameter(required = true)
	private String targetAnnoList;
	
	 /**
     * Location of the file.
     */
	@Parameter( defaultValue = "${project.build.outputDirectory}", property = "outputDirectory", required = true )
    private File outputDirectory;
	
	@Parameter(property = "generateGetAndSetMethod")
	private boolean generateGetAndSetMethod;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub
		GlobalConfig.setGenerateGetAndSetMethod(generateGetAndSetMethod);
		getLog().info("generateGetAndSetMethod="+generateGetAndSetMethod);
		getLog().info("scanPackageList="+scanPackageList);
		getLog().info("targetAnnoList="+targetAnnoList);
		getLog().info("outputDirectory="+outputDirectory);
		try {			
			List classpathElements = project.getCompileClasspathElements();
			for(int i=0;i<classpathElements.size();i++) {
				String fileName = (String)classpathElements.get(i);
				getLog().info(fileName);
				ClassSearchUtil.addClassPathToJavassist(fileName);
				ClassSearchUtil.addPathToClassPath(new File(fileName));
			}
		}catch (Exception e) {
			// 
			getLog().error(e);
			throw new MojoExecutionException("", e);
		}
		PrecompileMain.execute(scanPackageList, targetAnnoList, outputDirectory.getAbsolutePath());
	}

}
