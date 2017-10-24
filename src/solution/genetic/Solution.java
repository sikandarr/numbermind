package solution.genetic;

import java.io.*;
import java.util.*;

public class Solution
{
	//static long seed = new Random().nextLong();
	static long seed = 2001923711459688263L;
	static Random random = new Random(seed);
	static ArrayList<IncorrectDigit> incorrectDigits = new ArrayList<IncorrectDigit>();

	static class Guess
	{
		String guess;
		int correctDigits;

		Guess(String guess, int correctDigits)
		{
			this.guess = guess;
			this.correctDigits = correctDigits;
		}
	}

	static class Candidate
	{
		String candidate;
		int fitness;

		Candidate(String candidate, Guess[] guesses)
		{
			this.candidate = candidate;
			fitness = fitness(candidate, guesses);
		}

		@Override
		public String toString()
		{
			return candidate + " " + fitness;
		}
	}

	public static class IncorrectDigit
	{
		char digit;
		ArrayList<Integer> pos;

		IncorrectDigit(char digit)
		{
			this.digit = digit;
			pos = new ArrayList<Integer>();
		}

		public void initialize(char[] incorrectGuess)
		{
			for (int i = 0; i < incorrectGuess.length; i++)
			{
				if (digit == incorrectGuess[i])
					pos.add(i);
			}
		}

		public static ArrayList<IncorrectDigit> construct(String incorrectGuess)
		{
			ArrayList<IncorrectDigit> incorrectDigits = new ArrayList<IncorrectDigit>();
			char[] guess = incorrectGuess.toCharArray();
			for (int i = 0; i < guess.length; i++)
			{
				IncorrectDigit incorrectDigit = new IncorrectDigit(guess[i]);
				incorrectDigit.initialize(guess);
				incorrectDigits.add(incorrectDigit);
			}
			return incorrectDigits;
		}
	}

	public static boolean isCorrect(char c, int pos)
	{
		for (IncorrectDigit id : incorrectDigits)
		{
			if (id.digit == c)
			{
				for (int i : id.pos)
					if (i == pos)
						return false;
			}
		}
		return true;
	}

	public static String replaceAllIncorrect(String candidate)
	{
		char[] target = candidate.toCharArray();
		for (int i = 0; i < target.length; i++)
		{
			for (; !isCorrect(target[i], i);)
			{
				target[i] = (char) (random.nextInt(10) + 48);
			}
		}
		return String.valueOf(target);
	}

	public static int fitness(String candidate, Guess[] guesses)
	{
		int fitness = 0;
		for (int i = 0; i < guesses.length; i++)
		{
			int digits = determineCorrectDigits(candidate, guesses[i].guess);
			if (digits == guesses[i].correctDigits)
				fitness++;
		}
		return fitness;
	}

	public static int determineCorrectDigits(String target, String guess)
	{
		int correctDigits = 0;
		for (int i = 0; i < target.length() || i < guess.length(); i++)
		{
			if (target.charAt(i) == guess.charAt(i))
				correctDigits++;
		}
		return correctDigits++;
	}

	public static String mutate(char[] parentA, char[] parentB, int mutate)
	{
		char[] offspring = new char[parentA.length];
		for (int i = 0; i < parentA.length; i++)
		{
			int parent = random.nextInt(1);
			if (parent == 0)
				offspring[i] = parentA[i];
			else offspring[i] = parentB[i];
		}
		
		int[] mutations = new int[mutate];

		for (int i = 0; i < mutations.length; i++)
			mutations[i] = random.nextInt(parentA.length);

		for (int i = 0; i < mutations.length; i++)
		{
			offspring[mutations[i]] = (char) (random.nextInt(10) + 48);
		}

		return replaceAllIncorrect(String.valueOf(offspring));
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		File file = new File("input.txt");
		Scanner scan = new Scanner(file);
		int size = scan.nextInt();
		scan.nextLine();
		Guess[] guesses = new Guess[size];
		for (int i = 0; i < size; i++)
		{
			String line = scan.nextLine();
			String s[] = line.split(" ");
			guesses[i] = new Guess(s[0], new Integer(s[1]));
			if (guesses[i].correctDigits == 0)
				incorrectDigits.addAll(IncorrectDigit.construct(guesses[i].guess));
		}

		Candidate winner = null;
		ArrayList<Candidate> population = new ArrayList<Candidate>();
		final long startTime = System.currentTimeMillis();
		for (;;)
		{
			Candidate candidate = new Candidate(replaceAllIncorrect(generate(guesses[0].guess.length())), guesses);
			if (candidate.fitness > (guesses.length / 4))
				population.add(candidate);

			if (candidate.fitness == guesses.length)
			{
				winner = candidate;
				break;
			}

			if (candidate.fitness >= guesses.length / 2.5)
				break;
		}
		if (winner == null)
			for (;;)
			{
				Candidate parentA = population.get(random.nextInt(population.size()));
				Candidate parentB = population.get(population.size() - 1);
				String offspring = mutate(parentA.candidate.toCharArray(), parentB.candidate.toCharArray(), (int) (guesses.length/2.8));
				Candidate candidate = new Candidate(offspring, guesses);

				if (candidate.fitness >= parentB.fitness && !candidate.candidate.equals(parentB.candidate))
				{
					//System.out.println(candidate);
					population.add(candidate);
				}

				if (candidate.fitness == guesses.length)
				{
					winner = candidate;
					break;
				}

			}
		final long endTime = System.currentTimeMillis();
		System.out.println();
		System.out.println("Total execution time: " + (endTime - startTime));
		//System.out.println("Seed: " + seed + "ArrayList Size: " + population.size());
		System.out.println(winner.candidate);

	}

	public static String generate(int length)
	{
		char[] chars = "0123456789".toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++)
		{
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}
}