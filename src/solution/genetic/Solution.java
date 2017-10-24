package solution.genetic;

import java.io.*;
import java.util.*;

public class Solution
{
	static Random random = new Random();

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

	public static void displayCorrectDigits(String target, String guess)
	{
		int correctDigits = 0;
		for (int i = 0; i < target.length() || i < guess.length(); i++)
		{
			if (target.charAt(i) == guess.charAt(i))
				correctDigits++;
		}
		System.out.println(target + " | " + guess + " " + correctDigits);
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
			offspring[mutations[i]] = (char) (random.nextInt(10) + 48);

		return String.valueOf(offspring);
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
		}
		Candidate winner = null;
		ArrayList<Candidate> population = new ArrayList<Candidate>();
		for (;;)
		{
			Candidate candidate = new Candidate(generate(guesses[0].guess.length()), guesses);
			if (candidate.fitness > 0)
				population.add(candidate);

			if (candidate.fitness == guesses.length)
			{
				winner = candidate;
				break;
			}

			if (candidate.fitness >= (int) (guesses.length / 1.2))
				break;
		}
		if (winner == null)
			for (;;)
			{
				Candidate parentA = population.get(random.nextInt(population.size()));
				Candidate parentB = population.get(population.size() - 1);
				String offspring = mutate(parentA.candidate.toCharArray(), parentB.candidate.toCharArray(), 2);
				Candidate candidate = new Candidate(offspring, guesses);

				if (candidate.fitness >= parentB.fitness)
				{
					System.out.println(candidate);
					population.add(candidate);
				}

				if (candidate.fitness == guesses.length)
				{
					winner = candidate;
					break;
				}

			}
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