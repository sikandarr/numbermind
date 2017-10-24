package solution.genetic;

import java.io.*;
import java.util.*;

public class Solution
{
	// static long seed = 2001923711459688263L;
	static Random random = new Random();
	static HashMap<Character, ArrayList<Integer>> incorrectDigits = new HashMap<Character, ArrayList<Integer>>();

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

	public static void addIncorrectDigits(String incorrectGuess)
	{
		char[] guess = incorrectGuess.toCharArray();
		for (int i = 0; i < guess.length; i++)
		{
			ArrayList<Integer> pos = new ArrayList<Integer>();
			pos.add(i);
			if (incorrectDigits.get(guess[i]) == null)
				incorrectDigits.put(guess[i], pos);
			else incorrectDigits.get(guess[i]).addAll(pos);
		}
	}

	public static boolean isCorrect(char c, int pos)
	{
		for (int i : incorrectDigits.get(c))
			if (i == pos)
				return false;
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
			int digits = compareDigits(candidate, guesses[i].guess);
			if (digits == guesses[i].correctDigits)
				fitness++;
		}
		return fitness;
	}

	public static int compareDigits(String target, String guess)
	{
		int correctDigits = 0;
		for (int i = 0; i < target.length(); i++)
		{
			if (target.charAt(i) == guess.charAt(i))
				correctDigits++;
		}
		return correctDigits++;
	}

	public static String mutate(char[] parentA, char[] parentB, int rate)
	{
		char[] offspring = new char[parentA.length];
		for (int i = 0; i < parentA.length; i++)
		{
			int parent = random.nextInt(1);
			if (parent == 0)
				offspring[i] = parentA[i];
			else offspring[i] = parentB[i];
		}

		int[] mutations = new int[rate];

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
				addIncorrectDigits(guesses[i].guess);
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
				String offspring = mutate(parentA.candidate.toCharArray(), parentB.candidate.toCharArray(),
						(int) (guesses.length / 2.8));
				Candidate candidate = new Candidate(offspring, guesses);

				if (candidate.fitness >= parentB.fitness && !candidate.candidate.equals(parentB.candidate))
				{
					// System.out.println(candidate);
					population.add(candidate);
				}

				if (candidate.fitness == guesses.length)
				{
					winner = candidate;
					break;
				}

			}
		final long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime));
		System.out.println("Population: " + population.size());
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