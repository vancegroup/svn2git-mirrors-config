hourly = 'H * * * * '
daily = 'H H(0-7) * * *'

defaults = [
    frequency: daily,
    noMinimizeUrl: false
]
def fillDefaults(themap) {
    defaults.eachWithIndex{ defaultIt, i->
        if (!themap.containsKey(defaultIt.key)) { themap[defaultIt.key] = defaultIt.value }
    }
    return themap
}

// utility function for github-based mirrors
def gh(themap, therepo) {
    themap['git'] = 'git@github.com:' + therepo + '.git'
    themap['webpage'] = 'https://github.com/' + therepo
    return fillDefaults(themap)
}


// method to build a vancegroup-mirrors-hosted mirror: specify reponame in map, or defaults to project name
def vgmirrors(it) {
    if (it.containsKey('reponame')) {
        gh(it, 'vancegroup-mirrors/' + it.reponame)
    } else {
        gh(it, 'vancegroup-mirrors/' + it.projname)
    }
}

// Utility function to build the repetitive sourceforge svnroots
def sfsvnroot(proj) { 'https://' + proj + '.svn.sourceforge.net/svnroot/' + proj }
def gcsvnroot(proj) { 'http://' + proj + '.googlecode.com/svn/'}

def repos = [
    vgmirrors([
        projname: 'freeglut',
        svn: sfsvnroot('freeglut'),
        reponame: 'freeglut-mirror',
        frequency: hourly
    ]),
    vgmirrors([
        projname: 'cppdom',
        svn: sfsvnroot('xml-cppdom')
    ]),
    vgmirrors([
        projname: 'evoluspencil',
        svn: gcsvnroot('evoluspencil'),
        reponame: 'evolus-pencil-mirror'
    ]),
    vgmirrors([
        projname: 'gmtl',
        svn: sfsvnroot('ggt'),
        reponame: 'gmtl-mirror'
    ]),
    fillDefaults([
        projname: 'gmtl-testfresh',
        svn: sfsvnroot('ggt'),
        git: ''
    ]),
    vgmirrors([
        projname: 'h3dutil',
        svn: 'https://www.h3d.org:8090/H3DUtil/'
    ]),
    vgmirrors([
        projname: 'loki-lib',
        svn: sfsvnroot('loki-lib')
    ]),
    vgmirrors([
        projname: 'ode',
        svn: sfsvnroot('opende'),
        reponame: 'open-dynamics-engine-svnmirror'
    ]),
    vgmirrors([
        projname: 'osgAudio',
        svn: gcsvnroot('osgaudio'),
        reponame: 'osgAudio-mirror'
    ]),
    vgmirrors([
        projname: 'osgBullet',
        svn: gcsvnroot('osgbullet'),
        reponame: 'osgBullet-mirror'
    ]),
    vgmirrors([
        projname: 'osgWorks',
        svn: gcsvnroot('osgworks'),
        reponame: 'osgWorks-mirror'
    ]),
    vgmirrors([
        projname: 'mrbs',
        svn: 'https://mrbs.svn.sourceforge.net/svnroot/mrbs/mrbs/'
    ]),
    vgmirrors([
        projname: 'mrbs-relocated',
        svn: 'http://svn.code.sf.net/p/mrbs/code/mrbs/'
    ]),
    vgmirrors([
        projname: 'avr-libc',
        svn: 'http://svn.savannah.nongnu.org/svn/avr-libc/'
    ]),
    vgmirrors([
        projname: 'audiere',
        svn: sfsvnroot('audiere')
    ]),
    vgmirrors([
        projname: 'include-what-you-use',
        svn: gcsvnroot('include-what-you-use')
    ]),
    vgmirrors([
        projname: 'visual-understanding-environment',
        svn: sfsvnroot('tuftsvue')
    ]),
    vgmirrors([
        projname: 'collada-dom',
        svn: sfsvnroot('collada-dom'),
    ]),
    vgmirrors([
        projname: 'sofa-framework',
        svn: 'svn://scm.gforge.inria.fr/svnroot/sofa/',
    ]),
    vgmirrors([
        projname: 'bullet-physics',
        svn: gcsvnroot('bullet'),
    ]),
	vgmirrors([
        projname: 'osgworks',
        svn: gcsvnroot('osgworks'),
    ]),
    /*
    vgmirrors([
        projname: 'hapi',
        svn: 'https://www.h3d.org:8090/HAPI/'
    ]),*/
    
]

repos.each{
    def jobname = 'generated-svn2git-' + it.projname
    def cmd =  'export GITREMOTE=' + it.git + '\n' + '$WORKSPACE/driver.sh ' + it.svn
    def cronLine = it.frequency
    if (it.noMinimizeUrl) {
        cmd = 'export NOMINIMIZEURL=true \n' + cmd
    }
    println 'Job name: ' + jobname
    println 'Job cron line: ' + cronLine
    println 'Shell commands:\n' + cmd

    job {
        using 'template-job-svn2git'
        name jobname
        triggers {
            cron cronLine
        }
        steps {
            shell cmd
        }
    }

}
