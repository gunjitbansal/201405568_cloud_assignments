import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class translator {

	public static void main(String[] args) {
		
		try (BufferedReader br = new BufferedReader(new FileReader("32_bits.asm")))
		{
			PrintWriter writer = new PrintWriter("64_bits.asm", "UTF-8");

			String sCurrentLine;
			boolean flag=true;
			boolean lfbo=false;
			
			while ((sCurrentLine = br.readLine()) != null) 
			{
				if(sCurrentLine.matches("(.*).LFB0:(.*)"))
					{
						// System.out.println("--------------------------");
						lfbo=true;
						writer.println(sCurrentLine);
					}
				if(sCurrentLine.matches("(.*).LFE0:(.*)"))
				{
					// System.out.println("-------------0000000000-------------");
					lfbo=false;
				}
				if(lfbo)
				{
		//				System.out.println(sCurrentLine);
						if(sCurrentLine.matches("(.*)leal	4(.*)"))
						{
							// System.out.println("1");
							writer.println("	pushq	%rbp");
							continue;
						}
						
						if(flag && sCurrentLine.matches("(.*).cfi_def_cfa(.*)"))
						{
							// System.out.println("3");
							flag=false;
							writer.println("	.cfi_def_cfa_offset 16");
							writer.println("	.cfi_offset 6, -16");
							continue;
						}
						if(sCurrentLine.matches("(.*)leave(.*)"))
						{
							// System.out.println("2");
							writer.println(sCurrentLine);
							writer.println("	.cfi_def_cfa 7, 8");
							continue;
						}
						if(sCurrentLine.matches(".*movl	%esp, %ebp.*"))
						{
							// System.out.println("4");
							writer.println("	movq	%rsp, %rbp");
							continue;
						}
		
						if(sCurrentLine.matches(".*.cfi_escape 0xf,0x3,0x75,0x7c,0x6.*"))
							{
								// System.out.println("5");
								writer.println("	.cfi_def_cfa_register 6");
								continue;
							}
						if(sCurrentLine.matches("(.*)subl(.*)20(.*)"))
						{
							// System.out.println("6");
							writer.println("	subq	$16, %rsp.*");
							continue;
						}
						if(sCurrentLine.matches(".*movl(.*)4,.*"))
						{
							System.out.println("7");
							writer.println("	movl	$4, -12(%rbp)");
							writer.println("	movl	$2, -8(%rbp)");
							continue;
						}
						if(sCurrentLine.matches(".*movl(.*)20(.*)"))
						{
							System.out.println("8");
							writer.println("	movl	-12(%rbp), %eax");
							continue;
						}
					if(sCurrentLine.matches(".*imull.*"))
						{
						System.out.println("9");
							writer.println("	imull	-8(%rbp), %eax");
							continue;
						}
					if(sCurrentLine.matches(".*movl	(.*)eax, -12((.*)ebp).*"))
						{
						System.out.println("10");
							writer.println("	movl	%eax, -4(%rbp)");
							continue;
						}
					if(sCurrentLine.matches(".*subl(.*)8.*"))
						{
						System.out.println("11");
						writer.println("	movl	-4(%rbp), %eax");
						continue;
						}
					if(sCurrentLine.matches(".*pushl	-12((.*)ebp)(.*)"))
						{
						writer.println("	movl	%eax, %esi");
						System.out.println("12");
						continue;
						}
					if(sCurrentLine.matches(".*pushl(.*)LC0"))
						{
							System.out.println("13");
							writer.println("	movl	$.LC0, %edi");
							writer.println("	movl	$0, %eax");
							continue;
						}	
					if( sCurrentLine.matches("(.*).cfi_endproc(.*)") || sCurrentLine.matches("(.*)ret(.*)") ||  sCurrentLine.matches("(.*)call	printf(.*)") || sCurrentLine.matches(".*.cfi_startproc.*"))
						writer.println(sCurrentLine);
				}
				else
					writer.println(sCurrentLine);
			}
			writer.close();
			}
			catch (IOException e) {
			e.printStackTrace();
		} 

	}
}

